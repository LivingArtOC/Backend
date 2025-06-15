package livart.erp.domain.alarm.dto.request;

import livart.common.dto.enums.alarm.AdType;
import livart.common.dto.enums.alarm.SendType;
import lombok.Data;

import java.util.List;

@Data
public class EmailSendRequest {
    private AdType mailType;
    private String title;
    private Boolean onlyAllowedUser; // 수신 동의
    private List<Long> idList; // 회원 리스트
    private String content;
    private Boolean isRejected; // 수신 거부 포함 여부
    private String rejectMessage;
}
