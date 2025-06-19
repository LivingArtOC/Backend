package livart.erp.domain.promotion.dto.request;

import livart.common.dto.enums.coupon.CouponIssuedSearch;
import lombok.Getter;

@Getter
public class IssuedCouponResearchRequest {
    private CouponIssuedSearch key;
    private String keyword;
}
