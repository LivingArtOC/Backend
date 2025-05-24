package livart.erp.domain.product.productImage;

import jakarta.persistence.Lob;
import livart.common.dto.enums.product.ImageType;
import lombok.Getter;

@Getter
public class ProductImageRequest {
    private ImageType imageType;
    private Integer orderIndex;
    private String imageUrl;
    private String fileName;
    private String detailText;
}
