package livart.erp.domain.product.option;

import livart.common.dto.enums.product.StockStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class OptionCombinationRequest {
    private BigDecimal purchasePrice; // 옵션 매입가
    private BigDecimal price; // 옵션 가격
    private String optionCode;
    private Boolean isExposed;
    private StockStatus status;

    private List<OptionRequest> optionRequestList;
}
