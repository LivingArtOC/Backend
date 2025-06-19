package livart.erp.domain.product.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductAddSearchResponse {
    private Long productId;
    private Long categoryId;
    private String productName;
    private String productCode;
    private String thumbNailImgUrl;
}
