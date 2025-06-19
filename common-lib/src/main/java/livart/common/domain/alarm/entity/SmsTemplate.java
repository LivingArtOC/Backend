package livart.common.domain.alarm.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.alarm.*;
import livart.common.dto.request.SmsAutoDto;
import lombok.*;

@Table(name = "sms_template")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SmsTemplate extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SmsAutoType smsAutoType;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private SmsType type;

    private Integer sendStandardDate;
    private Integer resendDate;
    private Integer resendTime;
    private Boolean overNightSend;

    @Enumerated(EnumType.STRING)
    private SmsSendClass smsSendClass;

    private Boolean isAutoSendMember;
    private Boolean isAutoSendAdmin;

    @Lob
    private String memberContent;
    @Lob
    private String adminContent;
    
    private Long updatedBy;

    public void update(SmsAutoDto dto, Long updatedBy){
        this.smsAutoType = dto.getSmsAutoType();
        this.type = dto.getType();
        this.sendStandardDate = dto.getSendStandardDate();
        this.resendDate = dto.getResendDate();
        this.resendTime = dto.getResendTime();
        this.overNightSend = dto.getOverNightSend();
        this.smsSendClass = dto.getSmsSendClass();
        this.isAutoSendAdmin = dto.getIsAutoSendAdmin();
        this.isAutoSendMember = dto.getIsAutoSendMember();
        this.memberContent = dto.getMemberContent();
        this.adminContent = dto.getAdminContent();
        this.updatedBy = updatedBy;
    }
}
