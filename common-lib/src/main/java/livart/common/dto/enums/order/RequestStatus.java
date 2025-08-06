package livart.common.dto.enums.order;

public enum RequestStatus {
    REQUEST, // 신규 요청
    PROGRESS, // 진행중
    EXCHANGED, // 교환 완료
    REFUNDED, // 환불 완료
    RETURNED, // 반품 완료
    CANCELED, // 요청 취소됨
    ALL; // 통합 검색

    public static boolean contains(RequestStatus status) {
        return switch (status) {
            case REFUNDED, RETURNED, EXCHANGED -> true;
            default -> false;
        };
    }
}
