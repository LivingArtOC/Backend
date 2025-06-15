package livart.erp.domain.alarm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailInfoResponse {
    private String fromEMail;
    private Long allUserCount;
    private Long mkUserCount;
    private Long rejectUserCount;
    private String rejectUrl;
    private Boolean isRejected;
    private String lastRejectMessage;
    private String lastSendMessage;
}
