package livart.erp.domain.order.dto.request;

import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;

import java.util.Arrays;

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
    FAILED, // 결제 실패함
    DELETED, // 삭제 처리 됨
    EXCHANGE_RECEIPT, // 교환 접수
    RETURNING, // 반송중
    RE_DELIVERING, // 재배송중
    RETURN_RECEIPT, // 반품 접수
    SCHEDULED_RETURN, // 반송 예정
    PRODUCT_INSPECTION, // 제품 검수
    REFUND_RECEIPT // 환불 접수

}
