package livart.erp.domain.order.dto.request;

public enum OrderItemStatus {
    EXCHANGED, // 교환됨
    RETURNED , // 반품됨
    REFUNDED , // 환불됨
    CANCELED, // 취소됨
    CONFIRMED, // 구매 확정됨
    WAITING_SHIPMENT, // 출고 대기중
    DELIVERED, // 배송 완료됨
    PENDING, // 입금 대기중
    PAID, // 결제 완료됨
    FAILED // 결제 실패함
}
