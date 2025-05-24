package livart.shop.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import livart.common.domain.user.entity.User;
import livart.common.domain.user.repository.UserRepository;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.Auth.AuthTokens;
import livart.common.Auth.CustomUserDetails;
import livart.common.Auth.RefreshToken;
import livart.common.Auth.dto.request.LoginRequest;
import livart.common.Auth.repository.RefreshTokenRepository;
import livart.common.Auth.util.JwtTokenProvider;
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
import java.time.LocalDateTime;

import java.util.Date;

@Slf4j
public class ShopLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    public ShopLoginFilter(AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider,
                           RefreshTokenRepository refreshTokenRepository,
                           UserRepository userRepository,
                           LoginHistoryRepository loginHistoryRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.loginHistoryRepository = loginHistoryRepository;
        setFilterProcessesUrl("/api/shop/auth/login");   // 로그인 요청 URI
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
                String.valueOf(user.getId()), // loginId
                userDetails.getRole(),
                userDetails.getProvider(),
                new Date(System.currentTimeMillis() + jwtTokenProvider.getAccessExpiration())
        );

        String refreshToken = jwtTokenProvider.generate(
                String.valueOf(user.getId()),
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
                .site("SHOP")
                .createdAt(LocalDateTime.now())
                .build());
    }
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

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
                .site("SHOP")
                .failReason(failed.getMessage()) // ex: Bad credentials, User not found
                .createdAt(LocalDateTime.now())
                .build());
    }
}

