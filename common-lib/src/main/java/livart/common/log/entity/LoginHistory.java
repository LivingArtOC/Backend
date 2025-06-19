package livart.common.log.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Table(name = "login_history")
@Entity @Builder @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String loginId;

    private Boolean success;
    private Long userId;
    private String site;
    @Lob
    private String userAgent;
    private String failReason;
    private String ipAddress;
    private LocalDateTime createdAt;
}
