package livart.erp.domain.order.dto.request;

import livart.common.dto.enums.order.RequestStatus;
import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

@Getter
public class AsSearchRequest {
    private OrderSearchKey key;
    private String keyword;
    private RequestStatus status;
    private DateSearchDto requestDate;
}
