package livart.erp.domain.product.product.dto.request;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class PriceBatchChangeRequest {
    private List<Long> productIdList;
    private BigDecimal changePrice;
}
