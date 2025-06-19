package livart.common.domain.user.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.user.RestrictReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "restrict_ip")
@Entity @Builder @Getter
@AllArgsConstructor
@NoArgsConstructor
public class RestrictIp extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RestrictReason restrictReason;

    private String ipAddress;

    private LocalDateTime unlockTime;
}
