package livart.erp.domain.product.product.dto.request;

import livart.common.dto.enums.product.BrandType;
import livart.common.dto.enums.product.ProductSearchKey;
import lombok.Getter;

@Getter
public class ProductAddRequest {
    private ProductSearchKey key;
    private String keyword;
    private Long lastCategoryId;
    private BrandType brandType;
}
