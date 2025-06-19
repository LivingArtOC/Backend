package livart.common.log.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.alarm.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "sms_log")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SmsLog extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SmsForm smsForm;
    private Long templateId;

    @Setter
    private String senderNum; // 발신 번호
    private String phoneNum; // 수신 번호
    private String content;

    @Enumerated(EnumType.STRING)
    private SmsSendReserveType sendReserveType;

    @Enumerated(EnumType.STRING)
    private DefaultSmsType defaultSmsType; // 90 바이트 초과 시 메세지 전송 방법

    @Setter
    @Enumerated(EnumType.STRING)
    private SendStatus status;

    @Setter
    private Integer retryCount;
    private LocalDateTime reservedAt;
    private LocalDateTime sentAt;

    @Setter
    private String errorMessage;

    public void update(SendStatus status, String errorMessage, String senderNum, LocalDateTime sentAt){
        this.status = status;
        this.errorMessage = errorMessage;
        this.senderNum = senderNum;
        this.sentAt = sentAt;
    }
}
