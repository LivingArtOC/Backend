package livart.erp.security.config;

import livart.common.Auth.repository.RefreshTokenRepository;
import livart.common.Auth.util.JwtTokenProvider;
import livart.common.domain.setting.repository.AllowedAdminIpsRepository;
import livart.common.domain.user.repository.AdminRepository;
import livart.common.domain.user.repository.RestrictIpRepository;
import livart.common.domain.user.repository.UserRepository;
import livart.common.log.repository.LoginHistoryRepository;
import livart.erp.security.ErpJwtAuthenticationFilter;
import livart.erp.security.ErpLoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@Order(2)
public class ErpSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final LoginHistoryRepository loginHistoryRepository;
    private final AllowedAdminIpsRepository allowedAdminIpsRepository;
    private final AdminRepository adminRepository;
    private final RestrictIpRepository restrictIpRepository;

    public ErpSecurityConfig(JwtTokenProvider jwtTokenProvider,
                             RefreshTokenRepository refreshTokenRepository,
                             UserRepository userRepository,
                             AdminRepository adminRepository,
                             AuthenticationConfiguration authenticationConfiguration,
                             LoginHistoryRepository loginHistoryRepository,
                             AllowedAdminIpsRepository allowedAdminIpsRepository,
                             RestrictIpRepository restrictIpRepository,
                             @Qualifier("erpCorsConfigurationSource") CorsConfigurationSource corsConfigurationSource) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.authenticationConfiguration = authenticationConfiguration;
        this.corsConfigurationSource = corsConfigurationSource;
        this.restrictIpRepository = restrictIpRepository;
        this.loginHistoryRepository = loginHistoryRepository;
        this.allowedAdminIpsRepository = allowedAdminIpsRepository;
    }

    private final CorsConfigurationSource corsConfigurationSource; // 외부 주입

    @Bean(name = "erpFilterChain")
    public SecurityFilterChain erpFilterChain(HttpSecurity http,
                                              AuthenticationManager authenticationManager) throws Exception {
        http
                .securityMatcher("/api/erp/**")
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "api/erp/sms/**",
                                "/api/erp/auth/login/**",
                                "/api/erp/auth/refresh",
                                "/api/erp/client/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(AbstractHttpConfigurer::disable)
                .addFilterBefore(new ErpJwtAuthenticationFilter(jwtTokenProvider, userRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new ErpLoginFilter(
                        authenticationManager,
                        jwtTokenProvider,
                        refreshTokenRepository,
                        userRepository,
                        loginHistoryRepository,
                        allowedAdminIpsRepository,
                        restrictIpRepository,
                        adminRepository
                ), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
