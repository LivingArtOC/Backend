package livart.erp.domain.alarm.dto.request;

import livart.common.dto.enums.alarm.SendStatus;
import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

@Getter
public class KakaoLogSearchRequest {
    private KakaoLogSearchKey key;
    private String keyword;
    private SendStatus status;
    private DateSearchDto sentAt;
}
