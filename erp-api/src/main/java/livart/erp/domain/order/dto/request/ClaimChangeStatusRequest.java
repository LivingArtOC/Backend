package livart.erp.domain.order.dto.request;

import livart.common.dto.enums.order.ClaimReqStatus;
import lombok.Getter;

@Getter
public class ClaimChangeStatusRequest {
    private Long claimId;
    private ClaimReqStatus status;
}
