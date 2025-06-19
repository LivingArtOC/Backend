package livart.erp.domain.promotion.dto.request;

import livart.common.dto.enums.coupon.CouponDuplicateType;
import livart.common.dto.enums.coupon.PurchaseStandard;
import lombok.Getter;

@Getter
public class CouponSettingRequest {
    private CouponDuplicateType type;
    private PurchaseStandard purchaseStandard;
    private Boolean restoreCoupon;
}
