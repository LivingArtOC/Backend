package livart.erp.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public final class CookieUtil {
    private CookieUtil(){}

    public static void add(HttpServletResponse res, ResponseCookie c) {
        res.addHeader("Set-Cookie", c.toString());
    }

    public static ResponseCookie build(String name, String value,
                                       String domain, String path,
                                       boolean httpOnly, String sameSite,
                                       boolean secure, Duration maxAge) {
        return ResponseCookie.from(name, value)
                .domain(domain)
                .path(path)
                .httpOnly(httpOnly)
                .sameSite(sameSite)
                .secure(secure)
                .maxAge(maxAge)
                .build();
    }
}

