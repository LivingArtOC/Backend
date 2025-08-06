package livart.erp.domain.product.product.dto.response;

import livart.common.dto.enums.product.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductPriceResponse {
    private Long productId;
    private String productName;
    private String productCode;
    private ProductStatus status;
}
