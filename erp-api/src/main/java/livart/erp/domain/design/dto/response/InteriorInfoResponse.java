package livart.erp.domain.design.dto.response;

import livart.common.dto.enums.defaultSetting.DayType;
import livart.erp.domain.defaultSetting.policy.dto.response.CompanyInfoResponse;
import livart.erp.domain.design.dto.request.ImageListDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class InteriorInfoResponse {
    private String email;
    private String paxNum;
    private String directions;
    private String usageGuide;
    private List<ImageListDto> imageList;
    private Map<DayType, InteriorInfoResponse.TimeRange> hours = new EnumMap<>(DayType.class);

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TimeRange {
        private String start;
        private String end;
    }
}
