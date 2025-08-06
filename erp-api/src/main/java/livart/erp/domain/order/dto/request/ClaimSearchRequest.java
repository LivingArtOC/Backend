package livart.erp.domain.order.dto.request;

import livart.common.dto.enums.order.ClaimReqStatus;
import livart.common.dto.enums.order.RequestType;
import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

@Getter
public class ClaimSearchRequest {
    private OrderSearchKey key;
    private String keyword;
    private ClaimReqStatus status;
    private RequestType requestType;
    private DateSearchDto requestDate;
}
