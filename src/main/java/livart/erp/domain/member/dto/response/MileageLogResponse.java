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
public class MileageLogResponse {
    private String loginId;
    private MileageType type;
    private String description;
    private Integer amount;
    private LocalDateTime useTime;
}
