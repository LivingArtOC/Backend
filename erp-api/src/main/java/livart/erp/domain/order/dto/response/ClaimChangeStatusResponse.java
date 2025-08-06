package livart.erp.domain.order.dto.response;

import livart.common.dto.enums.order.ClaimReqStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaimChangeStatusResponse {
    private Long claimId;
    private ClaimReqStatus status;
    private LocalDateTime changedAt;
}
