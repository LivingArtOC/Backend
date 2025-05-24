package livart.common.domain.setting.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.domain.user.entity.Admin;
import lombok.*;

@Table(name = "allowed_admin_ip")
@Entity @Builder(toBuilder = true) @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AllowedAdminIp extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ipAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;
}
