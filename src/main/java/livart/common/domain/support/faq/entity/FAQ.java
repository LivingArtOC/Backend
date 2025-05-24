package livart.common.domain.support.faq.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.as.FAQStatus;
import livart.common.dto.enums.as.QuestionType;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "faq")
@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FAQ extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String questioner;
    private String respondent;
    @Setter
    private Boolean isAnswered; // 답변 여부

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @Enumerated(EnumType.STRING)
    private FAQStatus status;

    @Lob
    private String question;
    @Lob
    private String answer;
    private Long createdBy;
    private Long updatedBy;

    private LocalDateTime questionAt;
    private LocalDateTime answeredAt;

    public void update(String respondent, QuestionType type, FAQStatus status, String question, String answer, Long updatedBy, Boolean isAnswered, LocalDateTime answeredAt){
        this.respondent = respondent;
        this.type = type;
        this.status = status;
        this.question = question;
        this.answer = answer;
        this.updatedBy = updatedBy;
        this.isAnswered = isAnswered;
        this.answeredAt = answeredAt;
    }
}
