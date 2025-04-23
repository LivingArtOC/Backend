package livart.erp.domain.design.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class InteriorInfoRequest {
    private String email;
    private String paxNum;
    private String directions;
    private String usageGuide;
    private String operatingHours;
    private List<ImageListDto> imageList;
}
