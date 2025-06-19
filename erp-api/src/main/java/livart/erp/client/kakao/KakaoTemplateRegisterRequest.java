package livart.erp.client.kakao;


import livart.common.dto.enums.alarm.SmsAutoType;
import livart.common.dto.enums.alarm.SmsDivision;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
public class KakaoTemplateRegisterRequest {
    private String templateName;
    private String content;
    private String templateType;
    private Boolean isAdv;
    private Boolean securityFlag;
    private String language;
    private SmsDivision smsDivision;
    private SmsAutoType smsAutoType;
}
