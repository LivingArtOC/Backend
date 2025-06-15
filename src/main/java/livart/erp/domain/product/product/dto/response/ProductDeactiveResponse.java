package livart.erp.domain.product.product.dto.response;

import livart.common.dto.enums.product.ProductStatus;
import livart.common.dto.enums.product.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDeactiveResponse {
    private Long optionId;
    private String optionName;
    private String optionCode;
    private ProductStatus status;
}