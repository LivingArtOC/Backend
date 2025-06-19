package livart.erp.domain.alarm.dto.request;

import livart.common.dto.enums.alarm.SendType;
import livart.common.dto.enums.alarm.SmsSendReserveType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class SmsSendRequest {
    private Boolean onlyAllowedUser; // 수신 동의
    private List<Long> userList; // 회원 리스트
    private SmsSendReserveType sendReserveType;
    private LocalDateTime reserveDateTime;
    private String content;
}
