package livart.erp.domain.promotion.dto.request;

import livart.common.dto.enums.PurchaseStandard;
import lombok.Getter;

@Getter
public class CouponSettingRequest {
    private PurchaseStandard purchaseStandard;
    private Boolean restoreCoupon;
}
