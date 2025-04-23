package livart.erp.domain.defaultSetting.policy.dto.response;

import livart.common.dto.enums.Required;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TermsResponse {
    private Long termId;
    private String title;
    private Required required;
    private String content;
}
