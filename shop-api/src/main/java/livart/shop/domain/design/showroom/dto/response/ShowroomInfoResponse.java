package livart.shop.domain.design.showroom.dto.response;

import livart.common.domain.design.entity.InteriorsInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowroomInfoResponse {
    private String email;
    private String faxNum;
    private String directions;
    private String usageGuide;

    public static ShowroomInfoResponse from(InteriorsInfo i) {
        return ShowroomInfoResponse.builder()
                .email(i.getEmail())
                .faxNum(i.getFaxNum())
                .directions(i.getDirections())
                .usageGuide(i.getUsageGuide())
                .build();
    }
}