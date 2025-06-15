package livart.erp.domain.alarm.dto.request;

import lombok.*;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailSettingDto {
    private String senderName; // 발신자 이름
    private String fromEmail; // 발신 이메일 주소
    private String toEmail; // 수신 이메일 주소
    private String replyEmail; // 회신 이메일 주소
    private Boolean isAgreed; // 약관 동의 여부
}
