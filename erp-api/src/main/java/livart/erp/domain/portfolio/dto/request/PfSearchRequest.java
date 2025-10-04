package livart.erp.domain.portfolio.dto.request;

import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

@Getter
public class PfSearchRequest {
    private PfSearchKey key;
    private String keyword;
    private DateSearchDto deliveryDate;
}
