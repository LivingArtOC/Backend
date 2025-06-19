package livart.common.dto.request.product;

import jakarta.persistence.Column;
import livart.common.dto.enums.product.StockStatus;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class DetailedOptionRequest {
    private Integer orderIndex;
    private String optionName;
    private String valueName; // 옵션 값
}
