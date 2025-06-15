package livart.erp.domain.alarm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsInfoResponse {
    private BigDecimal smsPoint;
    private String senderNum; // 발신번호
    private String rejectNum; // 080 수신 거부 번호
    private String commercialMessage; // 광고성 문구
}
