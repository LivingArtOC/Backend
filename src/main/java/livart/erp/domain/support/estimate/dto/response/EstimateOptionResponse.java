package livart.erp.domain.support.estimate.dto.response;

import livart.common.dto.enums.product.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstimateOptionResponse {
    private Long estimateOptionId;
    private String optionName;
    private String valueName;
}
