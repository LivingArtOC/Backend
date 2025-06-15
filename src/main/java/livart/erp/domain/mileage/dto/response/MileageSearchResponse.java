package livart.erp.domain.mileage.dto.response;

import livart.common.dto.enums.user.MileageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class MileageSearchResponse {
    private Long logId;
    private String loginId;
    private String name;
    private MileageType type;
    private Integer amount;
    private LocalDate applyDate;
    private String agent;
    private String memo;
}
