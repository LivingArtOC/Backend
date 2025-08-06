package livart.common.dto.enums.alarm;

import java.util.Arrays;

public enum SmsAutoType {
    ALL,
    ORDER_DELIVERY, // 주문 배송 관련
    MEMBER, // 회원 관련
    SUB; // 부가 알림

    public static boolean contains(String value){
        return Arrays.stream(SmsAutoType.values())
                .anyMatch(t -> t.name().equals(value.toUpperCase()));
    }
}
