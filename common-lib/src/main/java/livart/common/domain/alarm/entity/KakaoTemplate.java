package livart.common.domain.alarm.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.alarm.KakaoTemplateStatus;
import livart.common.dto.enums.alarm.SendStatus;
import livart.common.dto.enums.alarm.SmsAutoType;
import livart.common.dto.enums.alarm.SmsDivision;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "kakao_template")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class KakaoTemplate extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String templateCode; // 중계사에서 지급하는 코드
    private String templateName; // 템플릿 이름
    private Boolean isAdv; // 광고성 템플릿 여부
    private String templateType; // BA, EX 등 (템플릿 유형)
    private String language;  // KOR
    private Boolean securityFlag; // 보안 템플릿 여부

    @Enumerated(EnumType.STRING)
    private SmsDivision smsDivision;

    @Enumerated(EnumType.STRING)
    private SmsAutoType smsAutoType;

    @Enumerated(EnumType.STRING)
    private KakaoTemplateStatus status;

    @Lob
    private String content;
    @Lob
    private String rejectReason;
    @Lob
    private String button; // 따옴표로 감싼 json으로 저장

    private LocalDateTime registerAt;
    private LocalDateTime approvedAt;


    public void approve(LocalDateTime approvedAt) {
        this.status = KakaoTemplateStatus.APPROVED;
        this.approvedAt = approvedAt;
    }

    public void reject(String reason) {
        this.status = KakaoTemplateStatus.REJECTED;
        this.rejectReason = reason;
    }
}
