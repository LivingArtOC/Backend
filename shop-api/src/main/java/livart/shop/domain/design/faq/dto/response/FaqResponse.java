package livart.shop.domain.design.faq.dto.response;

import livart.common.domain.support.faq.entity.FAQ;
import livart.common.dto.enums.as.QuestionType;
import lombok.Builder;
import lombok.Getter;
import java.time.ZoneId;

@Getter
@Builder
public class FaqResponse {
    private final Long id;
    private final QuestionType type;
    private final String question;
    private final String answer;
    private final Long updatedAt; // epoch millis (null 허용)

    public static FaqResponse from(FAQ faq) {
        Long updatedAtMillis = null;
        if (faq.getUpdatedAt() != null) {
            updatedAtMillis = faq.getUpdatedAt()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
        }
        return FaqResponse.builder()
                .id(faq.getId())
                .type(faq.getType())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .updatedAt(updatedAtMillis)
                .build();
    }
}