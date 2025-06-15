package livart.common.dto.enums.order;

import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;

public enum OrderStatus {
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
    REFUND_RECEIPT; // 환불 접수

    public static OrderStatus fromPending(OrderStatus status) {
        return switch (status) {
            case PENDING -> PENDING;
            case PAID -> PAID;
            case CANCELED -> CANCELED;
            default -> throw new CustomException(ErrorCode.INVALID_TYPE);
        };
    }

    public static OrderStatus fromPaid(OrderStatus status) {
        return switch (status) {
            case WAITING_SHIPMENT -> WAITING_SHIPMENT;
            case PAID -> PAID;
            case DELIVERED -> DELIVERED;
            case CONFIRMED -> CANCELED;
            default -> throw new CustomException(ErrorCode.INVALID_TYPE);
        };
    }

    public static OrderStatus fromExchange(OrderStatus status) {
        return switch (status) {
            case EXCHANGE_RECEIPT -> EXCHANGE_RECEIPT;
            case RETURNING -> RETURNING;
            case RE_DELIVERING -> RE_DELIVERING;
            case EXCHANGED -> EXCHANGED;
            default -> throw new CustomException(ErrorCode.INVALID_TYPE);
        };
    }

    public static OrderStatus fromReturn(OrderStatus status) {
        return switch (status) {
            case DELIVERED -> DELIVERED;
            case CONFIRMED -> CONFIRMED;
            case RETURN_RECEIPT -> RETURN_RECEIPT;
            case SCHEDULED_RETURN -> SCHEDULED_RETURN;
            case PRODUCT_INSPECTION -> PRODUCT_INSPECTION;
            case RETURNED -> RETURNED;
            default -> throw new CustomException(ErrorCode.INVALID_TYPE);
        };
    }

    public static OrderStatus fromRefund(OrderStatus status) {
        return switch (status) {
            case PAID -> PAID;
            case DELIVERED -> DELIVERED;
            case WAITING_SHIPMENT -> WAITING_SHIPMENT;
            case CONFIRMED -> CONFIRMED;
            case REFUND_RECEIPT -> REFUND_RECEIPT;
            case SCHEDULED_RETURN -> SCHEDULED_RETURN;
            case PRODUCT_INSPECTION -> PRODUCT_INSPECTION;
            case REFUNDED -> REFUNDED;
            default -> throw new CustomException(ErrorCode.INVALID_TYPE);
        };
    }
}
