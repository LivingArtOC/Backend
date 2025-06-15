package livart.erp.domain.alarm.dto.response;

import livart.common.dto.enums.alarm.DefaultSmsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsSettingResponse {
    private BigDecimal smsPoint;
    private String senderNum; // 발신번호
    private String rejectUrl; // 수신 거부 url
    private String rejectNum; // 080 수신 거부 번호
    private DefaultSmsType defaultSmsType;
}
