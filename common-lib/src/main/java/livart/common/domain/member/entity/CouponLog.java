package livart.common.domain.member.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.domain.promotion.entity.Coupon;
import livart.common.dto.enums.coupon.CouponStatus;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "coupon_use_log")
@Builder @Entity @Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponLog extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long orderId;
    private BigDecimal discountPrice;
    private String memo;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;
}
