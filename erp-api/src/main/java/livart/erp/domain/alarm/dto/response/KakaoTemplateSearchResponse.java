package livart.erp.domain.alarm.dto.response;

import livart.common.dto.enums.alarm.KakaoTemplateStatus;
import livart.common.dto.enums.alarm.SmsAutoType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoTemplateSearchResponse {
    private Long templateId;
    private String templateCode;
    private String templateName;
    private SmsAutoType smsAutoType;
    private String content;
    private LocalDate registerDate;
    private KakaoTemplateStatus status;
}
