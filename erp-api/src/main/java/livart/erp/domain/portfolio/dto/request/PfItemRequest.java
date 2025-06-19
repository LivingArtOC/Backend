package livart.erp.domain.portfolio.dto.request;

import livart.common.dto.enums.product.BrandType;
import lombok.Getter;

import java.util.List;

@Getter
public class PfItemRequest{
    private String productName;
    private String productImageUrl;
    private BrandType brandType;
    private Integer orderIndex;
    private List<OptionRequest> optionList;
}
