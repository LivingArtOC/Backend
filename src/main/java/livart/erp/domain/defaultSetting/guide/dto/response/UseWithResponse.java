package livart.erp.domain.defaultSetting.guide.dto.response;

import livart.common.dto.enums.defaultSetting.GuideType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder @Getter
@NoArgsConstructor
@AllArgsConstructor
public class UseWithResponse {
    private Long guideId;
    private GuideType type;
    private String content;
}
