package livart.erp.domain.order.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class UpdateStatusRequest {
    private List<Long> idList;
    private OrderItemStatus status;
}
