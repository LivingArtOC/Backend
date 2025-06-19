package livart.common.log.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.alarm.AdType;
import livart.common.dto.enums.alarm.EmailForm;
import livart.common.dto.enums.alarm.SendStatus;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "email_log")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailLog extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EmailForm emailForm;

    @Enumerated(EnumType.STRING)
    private AdType mailType;

    private Long userId;
    private Long templateId;
    private String recipientEmail;
    private String senderEmail;
    private String title;
    private String senderName;
    private Long senderAdmin;

    @Lob
    private String content;

    @Enumerated(EnumType.STRING)
    private SendStatus status;
    private LocalDateTime sentAt;
    private String errorMessage;
}
