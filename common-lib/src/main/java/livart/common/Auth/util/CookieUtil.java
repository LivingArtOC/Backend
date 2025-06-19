package livart.common.Auth.util;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    public static final String ACCESS_TOKEN_NAME = "accessToken";

    public Cookie createAccessTokenCookie(String token, long maxAgeInSeconds) {
        Cookie cookie = new Cookie(ACCESS_TOKEN_NAME, token);
        cookie.setPath("/");
        cookie.setHttpOnly(true); // JavaScript에서 접근 못함 (보안 강화)
        cookie.setSecure(true); // HTTPS 환경에서만 전송
        cookie.setMaxAge((int) maxAgeInSeconds);
        cookie.setDomain("prolink123.store"); // 실무에선 도메인 명시 (프론트와 동일)
        return cookie;
    }

    public Cookie deleteAccessTokenCookie() {
        Cookie cookie = new Cookie(ACCESS_TOKEN_NAME, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0); // 즉시 만료
        cookie.setDomain("prolink123.store");
        return cookie;
    }
}

