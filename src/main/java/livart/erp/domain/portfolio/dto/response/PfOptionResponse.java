package livart.erp.domain.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class PfOptionResponse {
    private Long optionId;
    private String optionName;
    private String optionValue;
}
