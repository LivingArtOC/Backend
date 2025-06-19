package livart.common.dto.request.product;

import livart.common.dto.enums.product.ColorType;
import lombok.Getter;

@Getter
public class ProductColorRequest {
    private ColorType colorType;
    private String colorCode;
    private Integer orderIndex;
}
