package livart.common.domain.alarm.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import lombok.*;
import software.amazon.awssdk.services.ses.endpoints.internal.Value;

@Table(name = "email_setting")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailSetting extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderName; // 발신자 이름
    private String fromEmail; // 발신 이메일 주소
    private String toEmail; // 수신 이메일 주소
    private String replyEmail; // 회신 이메일 주소
    private Boolean isAgreed; // 약관 동의 여부
    private Boolean isActive;
    private Long updatedBy;

    @Builder.Default
    private String rejectUrl = "수신 거부 URL";

    @Builder.Default
    private Boolean isRejected = true; // 수신 거부 포함 여부
    private String lastRejectMessage;
    private String lastSendMessage;

    public void update(String senderName, String fromEmail, String toEmail, String replyEmail, Boolean isAgreed, Long updatedBy){
        this.senderName = senderName;
        this.fromEmail = fromEmail;
        this.toEmail = toEmail;
        this.replyEmail = replyEmail;
        this.isAgreed = isAgreed;
        this.updatedBy = updatedBy;
    }

    public void updateMessage(String lastRejectMessage, String lastSendMessage, Boolean isRejected, Long updatedBy){
        this.lastRejectMessage = lastRejectMessage;
        this.lastSendMessage = lastSendMessage;
        this.isRejected = isRejected;
        this.updatedBy = updatedBy;
    }
}
