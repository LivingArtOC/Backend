package livart.common.dto.enums.order;

public enum OrderStatus {
    PENDING, // 결제 대기중
    EXCHANGED, // 교환됨
    RETURNED , // 반품됨
    REFUNDED , // 환불됨
    CANCELED, // 취소됨
    CONFIRMED, // 구매 확정됨
    PAID, // 결제 완료됨
    FAILED, // 결제 실패함
    DELETED // 삭제 처리
}
