package livart.erp.domain.product.productColor;

import livart.common.dto.enums.product.ColorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductColorResponse {
    private Long productColorId;
    private ColorType colorType;
    private String colorCode;
    private Integer orderIndex;
}
