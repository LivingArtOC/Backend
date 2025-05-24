package livart.erp.security;

import jakarta.servlet.http.HttpServletRequest;
import livart.common.Auth.AuthTokens;
import livart.common.Auth.CustomUserDetails;
import livart.common.Auth.RefreshToken;
import livart.common.Auth.dto.request.LoginRequest;
import livart.common.Auth.repository.RefreshTokenRepository;
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

        if (user.getStatus() != UserStatus.ACTIVE && user.getStatus() != UserStatus.DORMANT) {
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
    public void logout(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        refreshTokenRepository.deleteByUserId(customUserDetails.getId());
    }

    @Transactional
    public AuthTokens refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.TOKEN_INVALID);
        }

        String loginId = jwtTokenProvider.extractSubject(refreshToken);

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        RefreshToken savedToken = refreshTokenRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (!refreshToken.equals(savedToken.getRefreshToken())) {
            throw new CustomException(ErrorCode.TOKEN_INVALID);
        }

        String newAccessToken = jwtTokenProvider.generate(
                user.getLoginId(),
                user.getRole(),
                user.getProvider(),
                new Date(System.currentTimeMillis() + jwtTokenProvider.getAccessExpiration())
        );

        String newRefreshToken = jwtTokenProvider.generate(
                user.getLoginId(),
                user.getRole(),
                user.getProvider(),
                new Date(System.currentTimeMillis() + jwtTokenProvider.getRefreshExpiration())
        );

        savedToken.updateToken(newRefreshToken,
                LocalDateTime.now().plus(Duration.ofMillis(jwtTokenProvider.getRefreshExpiration())));
        refreshTokenRepository.save(savedToken);

        return AuthTokens.of(newAccessToken, newRefreshToken, "Bearer", 1800L);
    }

    public String extractFromAuthorizationHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.TOKEN_INVALID);
        }
        String token = header.substring(7);
        if (!StringUtils.hasText(token)) {
            throw new CustomException(ErrorCode.TOKEN_INVALID);
        }

        return token;
    }
}
