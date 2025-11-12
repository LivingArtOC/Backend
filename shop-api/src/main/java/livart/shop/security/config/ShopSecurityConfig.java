package livart.shop.security.config;

import livart.common.domain.social.entity.DynamicClientRegistrationRepository;
import livart.common.domain.user.repository.UserRepository;
import livart.common.log.repository.LoginHistoryRepository;
import livart.shop.security.entity.CustomOAuth2FailureHandler;
import livart.shop.security.entity.CustomOAuth2SuccessHandler;
import livart.shop.security.filter.ShopJwtAuthenticationFilter;
import livart.shop.security.filter.ShopLoginFilter;
import livart.common.Auth.repository.RefreshTokenRepository;
import livart.common.Auth.util.JwtTokenProvider;
import livart.shop.security.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@Order(1)
public class ShopSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomOAuth2FailureHandler customOAuth2FailureHandler;
    private final DynamicClientRegistrationRepository dynamicClientRegistrationRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    public ShopSecurityConfig(JwtTokenProvider jwtTokenProvider,
                              RefreshTokenRepository refreshTokenRepository,
                              UserRepository userRepository,
                              AuthenticationConfiguration authenticationConfiguration,
                              CustomOAuth2UserService customOAuth2UserService,
                              CustomOAuth2SuccessHandler customOAuth2SuccessHandler,
                              DynamicClientRegistrationRepository dynamicClientRegistrationRepository,
                              LoginHistoryRepository loginHistoryRepository,
                              CustomOAuth2FailureHandler customOAuth2FailureHandler,
                              @Qualifier("shopCorsConfigurationSource") CorsConfigurationSource corsConfigurationSource) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.authenticationConfiguration = authenticationConfiguration;
        this.customOAuth2UserService = customOAuth2UserService;
        this.corsConfigurationSource = corsConfigurationSource;
        this.customOAuth2SuccessHandler = customOAuth2SuccessHandler;
        this.dynamicClientRegistrationRepository = dynamicClientRegistrationRepository;
        this.loginHistoryRepository = loginHistoryRepository;
        this.customOAuth2FailureHandler = customOAuth2FailureHandler;
    }

    private final CorsConfigurationSource corsConfigurationSource; // 외부 주입

    @Bean(name = "shopFilterChain")
    public SecurityFilterChain shopFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource)) // 주입된 CORS 설정 사용
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/shop/client/sms/**",
                                "/api/shop/auth/**",
                                "/swagger-ui/**",
                                "/login/oauth2/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/api/shop/auth/signup/social" ).authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new ShopJwtAuthenticationFilter(jwtTokenProvider, userRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new ShopLoginFilter(authenticationConfiguration.getAuthenticationManager(),
                        jwtTokenProvider, refreshTokenRepository, userRepository,loginHistoryRepository), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth -> oauth
                        .clientRegistrationRepository(dynamicClientRegistrationRepository)
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(customOAuth2SuccessHandler)
                        .failureHandler(customOAuth2FailureHandler)
                );
        return http.build();
    }

    @Bean(name = "bCryptPasswordEncoder")
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
