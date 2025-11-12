package livart.erp.domain.product.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCouponSearchResponse {
    private Long couponId;
    private String couponName;
    private String discountRate;
    private boolean isIncluded;
}
