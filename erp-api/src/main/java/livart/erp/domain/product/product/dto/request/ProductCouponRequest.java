package livart.erp.domain.product.product.dto.request;

import livart.common.dto.enums.coupon.CouponIssuedSearch;
import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

@Getter
public class ProductCouponRequest {
    private CouponIssuedSearch key;
    private String keyword;
    private Long productId;
    private DateSearchDto couponRegister;
}
