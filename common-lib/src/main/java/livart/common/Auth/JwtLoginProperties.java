package livart.common.Auth;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "spring.jwt")
public class JwtLoginProperties {
    private String secretKey;
    private Long accessExpiration;
    private Long refreshExpiration;

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setAccessExpiration(Long accessExpiration) {
        this.accessExpiration = accessExpiration;
    }

    public void setRefreshExpiration(Long refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }
}
