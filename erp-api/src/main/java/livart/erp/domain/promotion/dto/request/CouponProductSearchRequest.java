package livart.erp.domain.promotion.dto.request;

import lombok.Getter;

@Getter
public class CouponProductSearchRequest {
    private ProductSearchKey key;
    private String keyword;
    private Long couponId;
}
