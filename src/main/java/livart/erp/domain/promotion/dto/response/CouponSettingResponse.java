package livart.erp.domain.promotion.dto.response;

import livart.common.dto.enums.coupon.CouponDuplicateType;
import livart.common.dto.enums.coupon.PurchaseStandard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponSettingResponse {
    private Long settingId;
    private CouponDuplicateType type;
    private PurchaseStandard purchaseStandard;
    private Boolean restoreCoupon;
}
