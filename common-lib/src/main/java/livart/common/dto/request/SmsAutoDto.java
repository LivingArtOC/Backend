package livart.common.dto.request;

import livart.common.dto.enums.alarm.SmsAutoType;
import livart.common.dto.enums.alarm.SmsSendClass;
import livart.common.dto.enums.alarm.SmsType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsAutoDto {
    private SmsAutoType smsAutoType;
    private SmsType type;
    private Integer sendStandardDate;
    private Integer resendDate;
    private Integer resendTime;
    private Boolean overNightSend;
    private SmsSendClass smsSendClass;
    private Boolean isAutoSendMember;
    private Boolean isAutoSendAdmin;
    private String memberContent;
    private String adminContent;
}
