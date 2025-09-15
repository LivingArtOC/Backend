package livart.erp.security.util;

import java.security.SecureRandom;
import java.util.Base64;

public final class CsrfTokenUtil {
    private static final SecureRandom RAND = new SecureRandom();
    public static String generate() {
        byte[] buf = new byte[32];
        RAND.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }
}
