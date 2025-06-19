package livart.erp.domain.support.faq.dto.request;

import livart.common.dto.enums.as.FAQStatus;
import livart.common.dto.enums.as.QuestionType;
import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

@Getter
public class FAQSearchRequest {
    private QuestionType type;
    private FAQStatus status;
    private DateSearchDto questionDate;
}
