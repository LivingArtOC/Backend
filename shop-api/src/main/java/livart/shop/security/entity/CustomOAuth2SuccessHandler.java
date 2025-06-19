package livart.shop.security.entity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import livart.common.Auth.CustomUserDetails;
import livart.common.Auth.RefreshToken;
import livart.common.Auth.repository.RefreshTokenRepository;
import livart.common.Auth.util.JwtTokenProvider;
import livart.common.domain.user.entity.Consumer;
import livart.common.domain.user.entity.User;
import livart.common.domain.user.repository.ConsumerRepository;
import livart.common.domain.user.repository.UserRepository;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final ConsumerRepository consumerRepository;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtTokenProvider.generate(
                String.valueOf(user.getId()),
                user.getRole(),
                user.getProvider(),
                new Date(System.currentTimeMillis() + jwtTokenProvider.getAccessExpiration())
        );

        String refreshToken = jwtTokenProvider.generate(
                String.valueOf(user.getId()),
                user.getRole(),
                user.getProvider(),
                new Date(System.currentTimeMillis() + jwtTokenProvider.getRefreshExpiration())
        );

        // 🔄 RefreshToken 저장 또는 갱신
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(user.getId())
                .map(token -> {
                    token.updateToken(refreshToken, LocalDateTime.now().plus(Duration.ofMillis(jwtTokenProvider.getRefreshExpiration())));
                    return token;
                })
                .orElse(RefreshToken.builder()
                        .userId(user.getId())
                        .refreshToken(refreshToken)
                        .expiredAt(LocalDateTime.now().plus(Duration.ofMillis(jwtTokenProvider.getRefreshExpiration())))
                        .build());

        refreshTokenRepository.save(refreshTokenEntity);

        Optional<Consumer> consumer = consumerRepository.findById(user.getId());


        // 🔁 리디렉트 URL 결정
        String targetUrl;
        if (!consumer.isPresent()) {
            targetUrl = String.format("http://localhost:3000/social-signup?accessToken=%s&refreshToken=%s",
                    accessToken, refreshToken);
        } else {
            targetUrl = String.format("http://localhost:3000/login-success?accessToken=%s&refreshToken=%s",
                    accessToken, refreshToken);
        }

        // 🔀 리디렉트 실행
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }
}

