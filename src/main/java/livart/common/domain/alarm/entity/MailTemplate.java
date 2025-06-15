package livart.common.domain.alarm.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.alarm.EmailAutoType;
import livart.common.dto.enums.alarm.EmailType;
import lombok.*;


@Table(name = "mail_template")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MailTemplate extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EmailAutoType emailAutoType;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private EmailType type;
    private Boolean isAutoSend;
    private String title;
    private Integer sendStandardDate;
    private Integer sendMethod;

    @Lob
    private String content;
    private Long updatedBy;

    public void update(EmailAutoType emailAutoType, Boolean isAutoSend, String title, Integer sendStandardDate,
                       Integer sendMethod, String content, Long updatedBy){
        this.emailAutoType = emailAutoType;
        this.isAutoSend = isAutoSend;
        this.title = title;
        this.sendStandardDate = sendStandardDate;
        this.sendMethod = sendMethod;
        this.content = content;
        this.updatedBy = updatedBy;
    }
}
