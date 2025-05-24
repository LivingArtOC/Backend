package livart.common.dto.enums.defaultSetting;

import livart.common.dto.enums.term.TermType;

import java.util.Arrays;

public enum GuideType {
    USE, WITHDRAW, AS, EXCHANGE, REFUND, DELIVERY;

    public static boolean contains(String value) {
        return Arrays.stream(GuideType.values())
                .anyMatch(t -> t.name().equals(value.toUpperCase()));
    }


}
