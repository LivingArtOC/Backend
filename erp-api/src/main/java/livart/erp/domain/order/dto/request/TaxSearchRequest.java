package livart.erp.domain.order.dto.request;

import livart.common.dto.enums.conv.TaxStatus;
import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

@Getter
public class TaxSearchRequest {
    private TaxSearchKey key;
    private String keyword;
    private TaxStatus status;
    private DateSearchDto requestDate;
}
