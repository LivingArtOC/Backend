package livart.erp.domain.product.productGuide;

import livart.common.dto.enums.product.ProductGuide;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductGuideInfoResponse {
    private Long guideId;
    private ProductGuide guide;
    private Integer orderIndex;
    private String imageUrl;
    private String fileName;
    private String text;
}
