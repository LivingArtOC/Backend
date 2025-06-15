package livart.common.domain.alarm.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.alarm.DefaultSmsType;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "sms_setting")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SmsSetting extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 10, scale = 2)
    private BigDecimal smsPoint; // 잔여 sms 포인트
    private String smsApiKey;
    private String smsApiSecret;
    private String senderNum; // 발신번호
    private String rejectUrl; // 수신 거부 url
    private String rejectNum; // 080 수신 거부 번호
    private String serviceNum; // 고객센터 기본 연결 번호

    @Builder.Default
    private String commercialMessage = "광고성 문구입니다";
    private Long updatedBy;

    @Builder.Default
    private Boolean isActive = false;

    @Enumerated(EnumType.STRING)
    private DefaultSmsType defaultSmsType; // 90 바이트 초과 시 메세지 전송 방법

    @Builder.Default
    private Boolean kakaoFirst = true;

    @Builder.Default
    private Boolean isLinkedKakao = false;

    public void update(String smsApiKey, String smsApiSecret, String senderNum, String rejectNum, String rejectUrl, String serviceNum, Long updatedBy){
        this.smsApiKey = smsApiKey;
        this.smsApiSecret = smsApiSecret;
        this.senderNum = senderNum;
        this.rejectNum = rejectNum;
        this.rejectUrl = rejectUrl;
        this.serviceNum = serviceNum;
        this.updatedBy = updatedBy;
    }

    public void updatePoint(BigDecimal updatePoint){
        this.smsPoint = updatePoint;
    }

    public void updateType(DefaultSmsType defaultSmsType, Long updatedBy){
        this.defaultSmsType = defaultSmsType;
        this.updatedBy = updatedBy;
    }
}
