package livart.erp.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import livart.common.Auth.AuthTokens;
import livart.common.Auth.CustomUserDetails;
import livart.common.Auth.RefreshToken;
import livart.common.Auth.dto.request.LoginRequest;
import livart.common.Auth.repository.RefreshTokenRepository;
import livart.erp.security.util.CookieProps;
import livart.erp.security.util.CookieUtil;
import livart.erp.security.util.CsrfTokenUtil;
import livart.common.Auth.util.JwtTokenProvider;
import livart.common.domain.setting.entity.AllowedAdminIp;
import livart.common.domain.setting.repository.AllowedAdminIpsRepository;
import livart.common.domain.user.entity.Admin;
import livart.common.domain.user.entity.User;
import livart.common.domain.user.repository.AdminRepository;
import livart.common.domain.user.repository.UserRepository;
import livart.common.dto.enums.user.Role;
import livart.common.dto.enums.user.UserStatus;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.log.entity.LoginHistory;
import livart.common.log.repository.LoginHistoryRepository;
import livart.common.service.GlobalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ErpAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final AllowedAdminIpsRepository allowedAdminIpsRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final GlobalService globalService;
    private final CookieProps cookieProps;

    @Transactional
    public AuthTokens login(LoginRequest request, HttpServletRequest httpServletRequest) throws IOException {

        if (!StringUtils.hasText(request.getLoginId()) || !StringUtils.hasText(request.getPassword())) {
            throw new CustomException(ErrorCode.NULL_INPUT_VALUE);
        }

        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String clientIp = getClientIp(httpServletRequest);

        validateAdmin(user, clientIp);


        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();


        String accessToken = jwtTokenProvider.generate(
                userDetails.getUsername(),
                userDetails.getRole(),
                userDetails.getProvider(),
                new Date(System.currentTimeMillis() + jwtTokenProvider.getAccessExpiration())
        );

        String refreshToken = jwtTokenProvider.generate(
                userDetails.getUsername(),
                userDetails.getRole(),
                userDetails.getProvider(),
                new Date(System.currentTimeMillis() + jwtTokenProvider.getRefreshExpiration())
        );

        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(user.getId())
                .map(token -> {
                    token.updateToken(refreshToken, LocalDateTime.now().plus(Duration.ofMillis(jwtTokenProvider.getRefreshExpiration())));
                    return token;
                }).orElse(RefreshToken.builder()
                        .userId(user.getId())
                        .refreshToken(refreshToken)
                        .expiredAt(LocalDateTime.now().plus(Duration.ofMillis(jwtTokenProvider.getRefreshExpiration())))
                        .build());

        refreshTokenRepository.save(refreshTokenEntity);

        loginHistoryRepository.save(LoginHistory.builder()
                .loginId(user.getLoginId())
                .userId(user.getId())
                .ipAddress(clientIp)
                .userAgent(httpServletRequest.getHeader("User-Agent"))
                .success(true)
                .failReason(null)
                .site("ERP")
                .createdAt(LocalDateTime.now())
                .build());

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return AuthTokens.of(accessToken, refreshToken, "Bearer", 1800L);
    }

    private void validateAdmin(User user, String clientIp) {
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.SUPER_ADMIN) {
            throw new CustomException(ErrorCode.ADMIN_ACCESS_DENIED);
        }

        if (user.getStatus() != UserStatus.ACTIVE && user.getStatus() != UserStatus.DORMANT && user.getStatus() != UserStatus.ADMIN_DORMANT) {
            throw new CustomException(ErrorCode.USER_STATUS_BLOCKED);
        }

        Admin admin = adminRepository.findById(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!admin.getLoginEnabled()) {
            throw new CustomException(ErrorCode.ADMIN_LOGIN_DISABLED);
        }

        List<AllowedAdminIp> allowedIps = allowedAdminIpsRepository.findByAdminId(user.getId());
        if (!allowedIps.isEmpty() && allowedIps.stream().noneMatch(ip -> ip.getIpAddress().equals(clientIp))) {
            throw new CustomException(ErrorCode.ACCESS_DENIED_BY_IP);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For: "clientIP, proxy1, proxy2" → 맨 앞 IP가 진짜
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

    @Transactional
    public void refreshByCookie(HttpServletRequest req, HttpServletResponse res) {
        String refresh = readCookie(req, "refresh_token");
        if (refresh == null) throw new CustomException(ErrorCode.TOKEN_INVALID);
        if (!jwtTokenProvider.validateToken(refresh)) throw new CustomException(ErrorCode.TOKEN_INVALID);

        String loginId = jwtTokenProvider.extractSubject(refresh);
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        RefreshToken saved = refreshTokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (!refresh.equals(saved.getRefreshToken())
                || saved.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        }

        // 새 토큰 생성 (회전)
        String newAccess = jwtTokenProvider.generate(
                user.getLoginId(), user.getRole(), user.getProvider(),
                new Date(System.currentTimeMillis() + jwtTokenProvider.getAccessExpiration())
        );
        String newRefresh = jwtTokenProvider.generate(
                user.getLoginId(), user.getRole(), user.getProvider(),
                new Date(System.currentTimeMillis() + jwtTokenProvider.getRefreshExpiration())
        );
        saved.updateToken(newRefresh,
                LocalDateTime.now().plus(Duration.ofMillis(jwtTokenProvider.getRefreshExpiration())));
        refreshTokenRepository.save(saved);

        // XSRF 토큰도 갱신(세션 고정화/재사용 완화)
        String xsrf = CsrfTokenUtil.generate();

        // 재발급 쿠키 내려주기
        issueAuthCookies(res, newAccess, newRefresh, xsrf);
    }

    // 로그아웃: 관리자 검증 + refresh 삭제 + 쿠키 무효화
    @Transactional
    public void logoutAndClearCookies(CustomUserDetails principal, HttpServletResponse res) {
        if (principal != null) {
            globalService.validateAdmin(principal);
            refreshTokenRepository.deleteByUserId(principal.getId());
        }
        clearAuthCookies(res);
    }

    private String readCookie(HttpServletRequest req, String name) {
        var cs = req.getCookies();
        if (cs == null) return null;
        for (var c : cs) if (name.equals(c.getName())) return c.getValue();
        return null;
    }

    // 쿠키(Access/Refresh/XSRF)
    private void issueAuthCookies(HttpServletResponse res, String access, String refresh, String xsrf) {
        var cd = cookieProps.domain();
        var secure = cookieProps.secure();

        CookieUtil.add(res, CookieUtil.build(
                "access_token", access, cd, "/",
                true, cookieProps.sameSite(), secure,
                Duration.ofMillis(jwtTokenProvider.getAccessExpiration())
        ));
        CookieUtil.add(res, CookieUtil.build(
                "refresh_token", refresh, cd, "/",
                true, cookieProps.sameSite(), secure,
                Duration.ofMillis(jwtTokenProvider.getRefreshExpiration())
        ));
        CookieUtil.add(res, CookieUtil.build(
                "XSRF-TOKEN", xsrf, cd, "/",
                false, cookieProps.sameSite(), secure,
                Duration.ofMillis(jwtTokenProvider.getAccessExpiration())
        ));
    }

    // 쿠키 무효화 (Max-Age=0, 경로 일치 중요)
    private void clearAuthCookies(HttpServletResponse res) {
        var cd = cookieProps.domain();
        var secure = cookieProps.secure();

        CookieUtil.add(res, CookieUtil.build(
                "access_token", "", cd, "/",
                true, cookieProps.sameSite(), secure, Duration.ZERO
        ));
        CookieUtil.add(res, CookieUtil.build(
                "refresh_token", "", cd, "/",
                true, cookieProps.sameSite(), secure, Duration.ZERO
        ));
        CookieUtil.add(res, CookieUtil.build(
                "XSRF-TOKEN", "", cd, "/",
                false, cookieProps.sameSite(), secure, Duration.ZERO
        ));
    }

    public Boolean checkAuth(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = extractTokenFromCookies(request, "access_token");

        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
            return true;
        }

        String refreshToken = extractTokenFromCookies(request, "refresh_token");
        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            String loginId = jwtTokenProvider.extractSubject(refreshToken);

            User user = userRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            String newAccessToken = jwtTokenProvider.generate(
                    user.getLoginId(),
                    user.getRole(),
                    user.getProvider(),
                    new Date(System.currentTimeMillis() + jwtTokenProvider.getAccessExpiration())
            );

            CookieUtil.add(response, CookieUtil.build(
                    "access_token", newAccessToken,
                    cookieProps.domain(), "/",
                    true, cookieProps.sameSite(), cookieProps.secure(),
                    Duration.ofMillis(jwtTokenProvider.getAccessExpiration())
            ));

            return true;
        }

        return false;
    }

    private String extractTokenFromCookies(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
