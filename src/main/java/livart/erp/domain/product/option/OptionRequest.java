package livart.erp.domain.product.option;

import livart.common.dto.enums.product.StockStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class OptionRequest {
    private String optionName;
    private String valueName; // 옵션 값
}
