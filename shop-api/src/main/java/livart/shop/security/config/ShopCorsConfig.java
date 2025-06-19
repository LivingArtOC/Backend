package livart.shop.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class ShopCorsConfig {

    @Bean(name = "shopCorsConfigurationSource")
    public CorsConfigurationSource shopCorsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("https://prolink123.store");
        config.addAllowedOrigin("http://localhost:8081");
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("http://52.78.209.179");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}