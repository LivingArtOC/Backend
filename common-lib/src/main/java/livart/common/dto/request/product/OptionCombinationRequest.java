package livart.common.dto.request.product;

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
    private String imageUrl;
    private String fileName;
    private List<OptionRequest> optionRequestList;
}
