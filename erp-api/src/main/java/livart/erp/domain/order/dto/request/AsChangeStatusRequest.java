package livart.erp.domain.order.dto.request;

import livart.common.dto.enums.order.RequestStatus;
import lombok.Getter;

@Getter
public class AsChangeStatusRequest {
    private Long asId;
    private RequestStatus status;
}
