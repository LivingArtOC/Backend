package livart.erp.domain.design.dto.request;

import livart.common.dto.enums.defaultSetting.DayType;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class InteriorInfoRequest {
    private String email;
    private String faxNum;
    private String counselNum;
    private String directions;
    private String usageGuide;
    private Map<DayType, TimeRange> hours;
    private List<ImageListDto> imageList;

    @Getter
    public static class TimeRange {
        private String start;
        private String end;
    }
}
