package livart.erp.domain.support.faq.dto.response;

import livart.common.dto.enums.as.FAQStatus;
import livart.common.dto.enums.as.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class FAQResponse {
    private Long faqId;
    private String questioner;
    private String respondent;
    private Boolean isAnswered;
    private QuestionType type;
    private FAQStatus status;
    private String question;
    private String answer;
    private LocalDate questionAt;
    private LocalDate answeredAt;
}
