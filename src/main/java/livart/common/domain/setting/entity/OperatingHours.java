package livart.common.domain.setting.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.defaultSetting.DayType;
import livart.common.dto.enums.defaultSetting.OperatingHoursType;
import lombok.*;

@Table(name = "operating_hour")
@Entity
@Builder @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OperatingHours extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OperatingHoursType operatingHoursType;

    @Enumerated(EnumType.STRING)
    private DayType dayType; // MONDAY ~ SUNDAY, HOLIDAY

    private String startTime;
    private String endTime;
    private Long updatedBy;
}
