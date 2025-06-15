package livart.erp.domain.product.product.dto.request;

import livart.common.dto.enums.product.ProductSearchKey;
import livart.common.dto.enums.product.ProductStatus;
import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

@Getter
public class ProductBatchRequest {
    private ProductSearchKey key;
    private String keyword;
    private DateSearchDto registerDate;
    private ProductStatus productStatus;
}
