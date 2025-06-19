package livart.erp.domain.product.productImage;

import livart.common.dto.enums.product.ImageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageResponse {
    private Long productImageId;
    private ImageType imageType;
    private Integer orderIndex;
    private String imageUrl;
    private String fileName;
    private String detailText;
}
