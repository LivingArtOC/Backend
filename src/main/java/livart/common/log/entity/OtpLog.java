package livart.common.log.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.OtpStatus;
import lombok.*;

import java.time.Instant;

@Table(name = "otp_logs")
@Entity @Builder @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OtpLog extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNum;
    private Long userId;
    private String otpCode;
    private Instant sentAt;

    @Enumerated(EnumType.STRING)
    private OtpStatus status;

    public void markUsed() {
        this.status = OtpStatus.USED;
    }
}
