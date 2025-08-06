package livart.erp.domain.order.dto.response;

import livart.common.dto.enums.order.ClaimReqStatus;
import livart.common.dto.enums.order.RequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaimSearchResponse {
    private Long claimId;
    private RequestType requestType;
    private LocalDate requestDate;
    private LocalDate completeDate;
    private String orderNum;
    private LocalDate orderDate;
    private String orderName;
    private String orderPhoneNum;
    private String orderItemName;
    private BigDecimal itemPrice;
    private String reason;
    private ClaimReqStatus status;
}
