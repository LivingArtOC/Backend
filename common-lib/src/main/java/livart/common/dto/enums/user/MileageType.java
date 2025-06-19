package livart.common.dto.enums.user;

public enum MileageType {
    PURCHASE_EARN, // 마일리지 적립(구매)
    ORDER_USAGE, // 주문 시 사용(차감)
    ORDER_REFUND, // 주문 환불
    GRANT_BY_ADMIN, // 관리자가 부여
    DEDUCT_BY_ADMIN, // 관리자가 차감
    ALL

}
