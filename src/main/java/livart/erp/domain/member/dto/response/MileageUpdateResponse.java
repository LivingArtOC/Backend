package livart.erp.domain.member.dto.response;

import livart.common.dto.enums.user.MileageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class MileageUpdateResponse {
    private Long targetId;
    private Integer afterMileage;
    private MileageType type;
    private String memo;
    private LocalDateTime updatedAt;
}
