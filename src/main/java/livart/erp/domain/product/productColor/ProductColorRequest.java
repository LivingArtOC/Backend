package livart.erp.domain.product.productColor;

import livart.common.dto.enums.product.ColorType;
import lombok.Getter;

@Getter
public class ProductColorRequest {
    private ColorType colorType;
    private String colorCode;
    private Integer orderIndex;
}
