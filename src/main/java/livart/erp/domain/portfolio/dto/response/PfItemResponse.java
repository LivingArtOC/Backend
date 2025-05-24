package livart.erp.domain.portfolio.dto.response;

import livart.common.dto.enums.product.BrandType;
import livart.erp.domain.portfolio.dto.request.OptionRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class PfItemResponse {
    private Long itemId;
    private String productName;
    private String productImageUrl;
    private BrandType brandType;
    private Integer orderIndex;
    private List<PfOptionResponse> optionList;
}
