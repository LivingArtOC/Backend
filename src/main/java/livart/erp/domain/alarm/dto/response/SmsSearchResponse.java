package livart.erp.domain.alarm.dto.response;

import livart.common.dto.enums.alarm.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsSearchResponse {
    private Long logId;
    private SmsForm smsForm;
    private DefaultSmsType defaultSmsType;
    private SmsSendReserveType reserveType;
    private String sender;
    private String recipient;
    private LocalDateTime sendAt;
    private SendStatus status;
}
