package livart.erp.domain.product.product.dto.request;

import lombok.Getter;

@Getter
public class ProductOrderUpdateRequest {
    private Long productId;
    private Integer manualOrder;
}
