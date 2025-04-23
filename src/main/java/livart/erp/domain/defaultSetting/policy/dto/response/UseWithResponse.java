package livart.erp.domain.defaultSetting.policy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UseWithResponse {
    private Long guideId;
    private String content;
}
