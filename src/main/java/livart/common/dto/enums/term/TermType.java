package livart.common.dto.enums.term;

import java.util.Arrays;

public enum TermType {
    SIGNUP,
    MARKETING,
    USE_POLICY,
    COURSE,
    ESTIMATE;

    public static boolean contains(String value) {
        return Arrays.stream(TermType.values())
                .anyMatch(t -> t.name().equals(value.toUpperCase()));
    }
}
