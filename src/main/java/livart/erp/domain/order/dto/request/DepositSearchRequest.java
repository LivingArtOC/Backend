package livart.erp.domain.order.dto.request;

import lombok.Getter;

@Getter
public class DepositSearchRequest {
    private DepositSearchKey key;
    private String keyword;

}
