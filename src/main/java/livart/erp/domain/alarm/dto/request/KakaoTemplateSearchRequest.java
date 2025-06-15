package livart.erp.domain.alarm.dto.request;

import livart.common.dto.enums.alarm.KakaoTemplateStatus;
import livart.common.dto.enums.alarm.SmsAutoType;
import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

@Getter
public class KakaoTemplateSearchRequest {
    private TemplateSearchKey key;
    private String keyword;
    private SmsAutoType type; // 전체는 null
    private KakaoTemplateStatus status;
    private DateSearchDto registerAt;
}
