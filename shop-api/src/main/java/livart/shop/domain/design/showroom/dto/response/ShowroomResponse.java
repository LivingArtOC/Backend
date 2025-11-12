package livart.shop.domain.design.showroom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowroomResponse {
    private ShowroomInfoResponse info;
    private List<ShowroomInteriorsResponse> interiors;
    private ShowroomHoursResponse hours;

    public static ShowroomResponse of(ShowroomInfoResponse info,
                                      List<ShowroomInteriorsResponse> interiors,
                                      ShowroomHoursResponse hours) {
        return ShowroomResponse.builder()
                .info(info)
                .interiors(interiors)
                .hours(hours)
                .build();
    }
}