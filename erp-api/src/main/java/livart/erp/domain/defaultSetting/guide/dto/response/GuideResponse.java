package livart.erp.domain.defaultSetting.guide.dto.response;

import livart.common.dto.enums.defaultSetting.GuideType;
import livart.erp.domain.defaultSetting.guide.dto.request.ImageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder @Getter
@AllArgsConstructor
@NoArgsConstructor
public class GuideResponse {
    private Long guideId;
    private GuideType type;
    private String content;
    private List<ImageDto> imageList;
}
