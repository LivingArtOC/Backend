package livart.erp.security.util;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.cookie")
public record CookieProps(
        String domain,
        boolean secure,
        String sameSite // "Lax", "Strict", "None"
) {}