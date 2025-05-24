package livart.erp.domain.support.inquiry.dto.response;

import livart.common.dto.enums.inquiry.InquiryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class InquiryStatusResponse {
    private Long inquiryId;
    private InquiryStatus status;
    private LocalDateTime updatedAt;
}
