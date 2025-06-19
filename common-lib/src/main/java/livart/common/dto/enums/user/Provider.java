package livart.common.dto.enums.user;

import java.lang.reflect.Array;
import java.util.Arrays;

public enum Provider {
    LOCAL, GOOGLE, KAKAO, NAVER, ALL;

    public static boolean containSocial(String value){
        return Arrays.stream(Provider.values())
                .anyMatch(t -> t.name().equals(value.toUpperCase()));
    }
}
