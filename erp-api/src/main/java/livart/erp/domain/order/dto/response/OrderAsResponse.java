package livart.erp.domain.order.dto.response;

import livart.common.dto.enums.order.PaymentMethod;
import livart.common.dto.enums.order.PaymentStatus;
import livart.common.dto.enums.order.RequestStatus;
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
public class OrderAsResponse {
    private Long orderId;
    private Long orderItemId;
    private Long userId;
    private LocalDate applyDate;
    private LocalDate completedDate;
    private String orderNum;
    private String orderName; // 주문자명
    private LocalDate orderDate;
    private String productName;
    private List<OrderOptionResponse> orderOption;
    private BigDecimal finalPrice;
    private PaymentMethod paymentMethod;
    private RequestStatus requestStatus;
    private String requestReason;
    private String depositor;
    private String account;
}
