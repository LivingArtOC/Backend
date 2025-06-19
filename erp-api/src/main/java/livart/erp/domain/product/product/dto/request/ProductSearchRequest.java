package livart.erp.domain.product.product.dto.request;

import livart.common.dto.enums.product.BrandType;
import livart.common.dto.enums.product.ProductSearchKey;
import livart.common.dto.enums.product.ProductStatus;
import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductSearchRequest {
    private ProductSearchKey key;
    private String keyword;
    private DateSearchDto registerDate;
    private ProductStatus productStatus;
    private Long categoryId;
    private BrandType brandType;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

}
