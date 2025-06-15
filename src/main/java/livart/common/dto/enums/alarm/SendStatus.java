package livart.common.dto.enums.alarm;

public enum SendStatus {
    PENDING, // 발송 대기
    SENT, // 발송 성공
    CANCELED, // 예약 취소
    FAILED, // 발송 실패
    FINAL_FAILED, // 최종 발송 실패(예약 3회 실패 후)
    ALL // 전체
}
