package livart.shop.domain.design.showroom.dto.response;

import livart.common.dto.enums.defaultSetting.DayType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.EnumMap;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShowroomHoursResponse {
    private Map<DayType, TimeRange> hours = new EnumMap<>(DayType.class);

    @Getter
    @AllArgsConstructor
    public static class TimeRange {
        private String start;
        private String end;
    }
}