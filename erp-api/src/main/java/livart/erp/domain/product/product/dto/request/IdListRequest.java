package livart.erp.domain.product.product.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class IdListRequest {
    private List<Long> productIdList;

}
