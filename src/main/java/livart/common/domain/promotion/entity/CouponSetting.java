package livart.common.domain.promotion.entity;

import jakarta.persistence.*;
import kotlinx.serialization.descriptors.SerialKind;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.PurchaseStandard;
import lombok.*;

@Table(name = "coupon_setting")
@Entity @Builder @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponSetting extends BaseTime {
    @Id
    private Long id = 1L;

    @Enumerated(EnumType.STRING)
    private PurchaseStandard standard;
    private Boolean restoreCoupon;
    private Long createdBy;
    private Long updatedBy;

    public void update(PurchaseStandard purchaseStandard, boolean restoreCoupon, Long updatedBy){
        this.standard = purchaseStandard;
        this.restoreCoupon = restoreCoupon;
        this.updatedBy = updatedBy;
    }
}
