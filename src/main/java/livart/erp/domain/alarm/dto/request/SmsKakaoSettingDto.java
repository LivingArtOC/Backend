package livart.erp.domain.alarm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsKakaoSettingDto {
    private String smsApiKey;
    private String smsApiSecret;
    private String senderNum; // 발신번호
    private String rejectUrl; // 수신 거부 url
    private String rejectNum; // 080 수신 거부 번호
    private String serviceNum;
}
