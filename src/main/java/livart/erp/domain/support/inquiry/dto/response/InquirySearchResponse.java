package livart.erp.domain.support.inquiry.dto.response;

import livart.common.dto.enums.inquiry.InquiryStatus;
import livart.common.dto.enums.inquiry.InquiryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class InquirySearchResponse {
    private Long inquiryId;
    private String questioner;
    private String respondent;
    private InquiryType type;
    private InquiryStatus status;
    private String title;
    private LocalDate questionAt;
    private LocalDate answeredAt;
    private Boolean isAnswered;
}
