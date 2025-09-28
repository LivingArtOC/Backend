package livart.erp.domain.order.dto.response;

import livart.common.dto.enums.order.ClaimReturnType;
import livart.common.dto.enums.order.RequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaimDetailResponse {
    private Long claimId;
    private RequestType requestType;
    private String invoiceNum;
    private ClaimReturnType returnType;
    private LocalDate returnDate;
    private String request;
    private BigDecimal paid;
    private BigDecimal couponDiscount;
    private BigDecimal deliveryFee;
    private BigDecimal returnFee;
    private String refundAcc;
}

