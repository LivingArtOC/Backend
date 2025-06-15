package livart.erp.domain.alarm.dto.response;

import livart.common.dto.enums.alarm.SendStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoLogSearchResponse {
    private Long logId;
    private String recipientPhone;
    private String templateName;
    private LocalDateTime sentAt;
    private String failReason;
    private SendStatus sendStatus;
}
