package livart.erp.domain.promotion.dto.request;

import livart.common.dto.enums.coupon.*;
import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

@Getter
public class CouponSearchRequest {
    private CouponIssuedSearch key;
    private String keyword;
    private DateSearchDto couponRegister;
    private DateSearchDto couponExpire;
    private CouponType couponType;
    private IssuedMethod issuedMethod;
    private IssuedStatus issuedStatus;
    private CouponDiscountType couponDiscountType;
}
