package livart.erp.domain.support.inquiry.dto.request;

import livart.common.dto.enums.inquiry.InquiryStatus;
import livart.common.dto.enums.inquiry.InquiryType;
import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

@Getter
public class InquirySearchRequest {
    private SearchKey key;
    private String keyword;
    private InquiryType type;
    private Boolean isAnswered;
    private InquiryStatus status;
    private DateSearchDto answeredDate;
    private DateSearchDto questionDate;
}
