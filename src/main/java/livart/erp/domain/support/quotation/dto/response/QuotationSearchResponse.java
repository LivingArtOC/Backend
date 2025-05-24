package livart.erp.domain.support.quotation.dto.response;

import livart.common.dto.enums.quotation.QuotationStatus;
import livart.erp.domain.portfolio.dto.request.PfRegisterRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuotationSearchResponse {
    private Long quotationId;
    private String title;
    private String picName;
    private String picPhoneNum;
    private String proposer;
    private LocalDate registerDate;
    private QuotationStatus status;
}
