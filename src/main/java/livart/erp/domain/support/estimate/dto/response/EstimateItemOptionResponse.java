package livart.erp.domain.support.estimate.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstimateItemOptionResponse {
    private Long optionId;
    private String optionName;
    private String optionValue;
    private BigDecimal optionPrice;
    private String optionCode;
}
