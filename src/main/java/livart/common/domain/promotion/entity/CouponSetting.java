package livart.common.domain.promotion.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.coupon.CouponDuplicateType;
import livart.common.dto.enums.coupon.PurchaseStandard;
import lombok.*;

@Table(name = "coupon_setting")
@Entity @Builder @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponSetting extends BaseTime {
    @Id
    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private CouponDuplicateType type = CouponDuplicateType.INDIVIDUAL;

    @Enumerated(EnumType.STRING)
    private PurchaseStandard standard;
    private Boolean restoreCoupon;
    private Long createdBy;
    private Long updatedBy;

    public void update(CouponDuplicateType type, PurchaseStandard purchaseStandard, Boolean restoreCoupon, Long updatedBy){
        this.type = type;
        this.standard = purchaseStandard;
        this.restoreCoupon = restoreCoupon;
        this.updatedBy = updatedBy;
    }
}
