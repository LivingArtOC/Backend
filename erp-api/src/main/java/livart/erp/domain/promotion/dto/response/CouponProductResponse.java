package livart.erp.domain.promotion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.ses.endpoints.internal.Value;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponProductResponse {
    private Long productId;
    private String productCode;
    private String productName;
    private String salePrice;
    private boolean isIncluded;
}
