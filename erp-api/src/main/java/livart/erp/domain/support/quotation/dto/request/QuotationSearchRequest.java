package livart.erp.domain.support.quotation.dto.request;

import livart.common.dto.enums.quotation.QuotationStatus;
import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

@Getter
public class QuotationSearchRequest {
    private SearchKey key;
    private String keyword;
    private QuotationStatus status;
    private DateSearchDto registerDate;
}
