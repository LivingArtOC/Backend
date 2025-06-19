package livart.common.log.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.domain.alarm.entity.KakaoTemplate;
import livart.common.dto.enums.alarm.SendStatus;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "kakao_log")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class KakaoLog extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long templateId;
    private String templateName;

    private String recipientPhone;        // 수신 전화번호

    @Lob
    private String sendContent;           // 실제 발송한 메시지 (치환 후 결과)

    @Enumerated(EnumType.STRING)
    private SendStatus sendStatus;

    @Lob
    private String failReason;            // 실패 시 사유 (에러 메시지 등)

    private LocalDateTime sentAt;

}
