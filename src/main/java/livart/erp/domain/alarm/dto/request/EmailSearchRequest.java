package livart.erp.domain.alarm.dto.request;

import livart.common.dto.enums.alarm.EmailForm;
import livart.common.dto.request.DateSearchDto;
import lombok.Getter;
import software.amazon.awssdk.services.ses.endpoints.internal.Value;

@Getter
public class EmailSearchRequest {
    private EmailSearchKey key;
    private String keyword;
    private EmailForm emailForm;
    private DateSearchDto sentDate;
}
