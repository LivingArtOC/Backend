package livart.common.dto.enums.alarm;

public enum SmsSendClass {
    INTEGRATION, // 주문 건 기준 1회 발송
    INDIVIDUAL // 부분 배송 시 배송 건 별로 각각 발송
}
