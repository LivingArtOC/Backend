package livart.common.log.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.domain.user.entity.User;
import livart.common.dto.enums.user.UserStatus;
import lombok.*;

@Table(name = "user_status_log")
@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatusLog extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UserStatus updateStatus;
    private Boolean byAdmin;
    private String agent;
    private String reason;
    private String withdrawIpAdress;
    private Long createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
