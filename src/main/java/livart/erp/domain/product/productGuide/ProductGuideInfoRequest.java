package livart.erp.domain.product.productGuide;

import livart.common.dto.enums.product.ProductGuide;
import lombok.Getter;

@Getter
public class ProductGuideInfoRequest {
    private ProductGuide guide;
    private Integer orderIndex;
    private String imageUrl;
    private String fileName;
    private String text;
}
