package livart.erp.domain.support.estimate.dto.response;

import livart.common.dto.enums.product.BrandType;
import livart.common.dto.enums.product.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstimateProductResponse {
    private Long itemId;
    private String productName;
    private String thumbNailImgUrl;
    private BrandType brand;
    private Integer quantity;
    private BigDecimal salePrice;
    private BigDecimal originalPrice;
    private String message;
    private List<EstimateOptionResponse> options;
}