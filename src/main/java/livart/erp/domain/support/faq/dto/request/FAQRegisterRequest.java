package livart.erp.domain.support.faq.dto.request;

import livart.common.dto.enums.as.QuestionType;
import lombok.Getter;

@Getter
public class FAQRegisterRequest {
    private QuestionType type;
    private String question;
    private String answer;
}
