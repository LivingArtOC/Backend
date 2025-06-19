package livart.common.domain.member.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.domain.promotion.entity.Coupon;
import livart.common.domain.user.entity.User;
import lombok.*;

@Table(name = "user_coupon")
@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
