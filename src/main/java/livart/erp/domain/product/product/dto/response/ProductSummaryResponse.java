package livart.erp.domain.product.product.dto.response;

import livart.common.dto.enums.product.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSummaryResponse {
    private Long productId;
    private Long optionId;
    private String optionCode;
    private String imageUrl;
    private String productName;
    private String optionName;
    private StockStatus status;
}
