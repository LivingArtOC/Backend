package livart.erp.domain.support.estimate.dto.request;

import livart.common.dto.enums.product.BrandType;
import livart.common.dto.enums.product.StockStatus;
import livart.erp.domain.support.estimate.dto.response.EstimateOptionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class EstimateProductRequest {
    private Long productId;
    private String optionCode;
    private String hashCode;
    private String thumbNailImgUrl;
    private Integer quantity;
}
