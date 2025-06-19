package livart.common.dto.request.product;

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
