package livart.erp.domain.order.dto.response;

import livart.common.dto.enums.conv.TaxStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaxSearchResponse {
    private Long taxId;
    private LocalDate requestDate;
    private LocalDate issuedDate;
    private String name;
    private String phoneNum;
    private TaxStatus status;
    private String failReason;
    private Boolean isIssued;
    private String pdfUrl;
}
