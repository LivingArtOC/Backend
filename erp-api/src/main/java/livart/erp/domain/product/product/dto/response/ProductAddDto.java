package livart.erp.domain.product.product.dto.response;

import livart.common.dto.enums.product.BrandType;
import livart.common.dto.enums.product.StockStatus;
import livart.erp.domain.product.option.OptionAddResponse;
import livart.erp.domain.product.option.OptionCombinationResponse;
import livart.erp.domain.product.option.OptionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductAddDto {
    private Long productId;
    private String productName;
    private String productCode;
    private String thumbNailImgUrl;
    private BrandType brand;
    private StockStatus status;
    private Integer quantity;
    private BigDecimal unitDelPrice;
    private BigDecimal supplyPrice;
    private String replaceComment;
    private List<OptionAddResponse> options;
}
