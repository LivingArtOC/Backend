package livart.erp.domain.defaultSetting.guide.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class GuideRequest {
    private String content;
    private List<ImageDto> imageList;
}
