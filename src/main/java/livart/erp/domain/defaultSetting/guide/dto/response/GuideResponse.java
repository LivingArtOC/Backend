package livart.erp.domain.defaultSetting.guide.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true) @Getter
@AllArgsConstructor
@NoArgsConstructor
public class GuideResponse {

    private Long guideId;
    private String title;
    private String content;
    private String image_url;
    private Long updateBy;
}
