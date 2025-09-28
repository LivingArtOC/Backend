package livart.erp.domain.order.dto.request;

import livart.common.dto.enums.order.ClaimReturnType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class ClaimDetailRequest {
    private Long claimId;
    private String invoiceNum;
    private ClaimReturnType returnType;
    private LocalDate returnDate;
    private String request;
    private BigDecimal returnFee;
}
