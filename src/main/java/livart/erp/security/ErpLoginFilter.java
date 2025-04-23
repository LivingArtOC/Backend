package livart.erp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import livart.common.Auth.AuthTokens;
import livart.common.Auth.CustomUserDetails;
import livart.common.Auth.RefreshToken;
import livart.common.Auth.dto.request.LoginRequest;
import livart.common.Auth.repository.RefreshTokenRepository;
import livart.common.Auth.util.JwtTokenProvider;
import livart.common.domain.setting.entity.AllowedAdminIps;
import livart.common.domain.setting.repository.AllowedAdminIpsRepository;
import livart.common.domain.user.entity.Admin;
import livart.common.domain.user.entity.User;
import livart.common.domain.user.repository.AdminRepository;
import livart.common.domain.user.repository.UserRepository;
import livart.common.dto.enums.Role;
import livart.common.dto.enums.UserStatus;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.log.entity.LoginHistory;
import livart.common.log.repository.LoginHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@Slf4j
public class ErpLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final AllowedAdminIpsRepository allowedAdminIpsRepository;
    private final AdminRepository adminRepository;

    public ErpLoginFilter(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          RefreshTokenRepository refreshTokenRepository,
                          UserRepository userRepository,
                          LoginHistoryRepository loginHistoryRepository,
                          AllowedAdminIpsRepository allowedAdminIpsRepository,
                          AdminRepository adminRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.loginHistoryRepository = loginHistoryRepository;
        this.allowedAdminIpsRepository = allowedAdminIpsRepository;
        setFilterProcessesUrl("/api/erp/auth/login");   // 로그인 요청 URI
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            if (request.getInputStream() == null || request.getContentLength() <= 0) {
                throw new CustomException(ErrorCode.NULL_INPUT_VALUE);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

            if (loginRequest.getLoginId() == null || loginRequest.getPassword() == null) {
                throw new CustomException(ErrorCode.NULL_INPUT_VALUE);
            }

            User user = userRepository.findByLoginId(loginRequest.getLoginId())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            if (user.getRole() != Role.ADMIN && user.getRole() != Role.SUPER_ADMIN) {
                throw new CustomException(ErrorCode.ADMIN_ACCESS_DENIED); // 401 or 403
            }

            if (user.getStatus() != UserStatus.ACTIVE && user.getStatus() != UserStatus.DORMANT){
                throw new CustomException(ErrorCode.USER_STATUS_BLOCKED);
            }

            Admin admin = adminRepository.findById(user.getId())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            if (admin.getLoginEnabled() != true){
                throw new CustomException(ErrorCode.ADMIN_LOGIN_DISABLED);
            }

            String clientIp = getClientIp(request);

            List<AllowedAdminIps> allowedAdminIpsList = allowedAdminIpsRepository.findByAdminId(user.getId());

            if (!allowedAdminIpsList.isEmpty()){
                boolean isAllowed = allowedAdminIpsList.stream()
                        .anyMatch(allowed -> allowed.getIpAddress().equals(clientIp));
                if (isAllowed != true){
                    throw new CustomException(ErrorCode.ACCESS_DENIED_BY_IP);
                }
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword());

            log.info("로그인 시도 - loginId: {}", loginRequest.getLoginId());

            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            log.error("로그인 요청 처리 실패", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) throws IOException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtTokenProvider.generate(
                userDetails.getUsername(), // loginId
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

        // 🔄 리프레시 토큰 저장
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(userDetails.getId())
                        .map(token -> {
                            token.updateToken(refreshToken, LocalDateTime.now().plus(Duration.ofMillis(jwtTokenProvider.getRefreshExpiration())));
                            return token;
                        }).orElse(RefreshToken.builder()
                        .userId(userDetails.getId())
                        .refreshToken(refreshToken)
                        .expiredAt(LocalDateTime.now().plus(Duration.ofMillis(jwtTokenProvider.getRefreshExpiration())))
                        .build());

        refreshTokenRepository.save(refreshTokenEntity);

        // 🛡️ 인증 정보 SecurityContext 에 등록
        SecurityContextHolder.getContext().setAuthentication(authentication);

        response.setHeader("Authorization", "Bearer " + accessToken);

        AuthTokens authTokens = AuthTokens.of(accessToken, refreshToken, "Bearer", 1800L);
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getWriter(), authTokens);

        log.info("로그인 성공: {}", userDetails.getUsername());

        loginHistoryRepository.save(LoginHistory.builder()
                .loginId(user.getLoginId())
                .userId(user.getId())
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .success(true)
                .failReason(null)
                .site("ERP")
                .attemptedAt(Instant.now())
                .build());

    }
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        log.warn("로그인 실패: {}", failed.getMessage(), failed);

        String loginId = request.getParameter("loginId");
        if (loginId == null){
            loginId = "(unknown)";
        }

        loginHistoryRepository.save(LoginHistory.builder()
                .loginId(loginId)
                .userId(null)
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .success(false)
                .site("ERP")
                .failReason(failed.getMessage()) // ex: Bad credentials, User not found
                .attemptedAt(Instant.now())
                .build());
    }

    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

