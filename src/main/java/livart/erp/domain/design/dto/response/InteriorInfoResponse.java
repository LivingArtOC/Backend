package livart.erp.domain.design.dto.response;

import livart.erp.domain.design.dto.request.ImageListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class InteriorInfoResponse {
    private String email;
    private String paxNum;
    private String directions;
    private String usageGuide;
    private String operatingHours;
    private List<ImageListDto> imageList;
}
