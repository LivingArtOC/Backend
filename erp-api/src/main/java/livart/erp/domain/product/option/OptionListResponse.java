package livart.erp.domain.product.option;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptionListResponse {
    private List<DetailedOptionResponse> detailedOptionResponse;
    private List<OptionCombinationResponse> combinationResponse;
}
