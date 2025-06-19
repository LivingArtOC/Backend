package livart.erp.domain.order.dto.request;

import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

@Getter
public class OrderSearchRequest {
    private OrderSearchKey key;
    private String keyword;
    private DateSearchDto orderDate;
}
