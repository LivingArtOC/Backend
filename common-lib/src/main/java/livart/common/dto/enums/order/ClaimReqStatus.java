package livart.common.dto.enums.order;

import livart.common.dto.enums.alarm.SmsAutoType;

import java.util.Arrays;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public enum ClaimReqStatus {
    REQUEST, // 신규 요청
    PROGRESS, // 진행중
    COMPLETED, // 진행 완료
    CANCELED, // 요청 취소됨
    REJECTED, // 요청 거절됨
    ALL; // 통합 검색

    public static boolean contains(ClaimReqStatus status) {
        return switch (status) {
            case REQUEST, PROGRESS, COMPLETED -> true;
            default -> false;
        };
    }
}
