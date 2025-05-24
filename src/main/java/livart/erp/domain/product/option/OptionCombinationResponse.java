package livart.erp.domain.product.option;

import livart.common.dto.enums.product.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptionCombinationResponse {
    private Long optionId;
    private BigDecimal purchasePrice; // 옵션 매입가
    private BigDecimal price; // 옵션 가격
    private String optionCode;
    private Boolean isExposed;
    private StockStatus status;

    private List<OptionResponse> optionResponseList;
}
