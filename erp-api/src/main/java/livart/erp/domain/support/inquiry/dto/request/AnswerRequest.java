package livart.erp.domain.support.inquiry.dto.request;

import livart.common.dto.enums.inquiry.InquiryStatus;
import lombok.Getter;

@Getter
public class AnswerRequest {
    private String answer;
    private InquiryStatus status;
}
