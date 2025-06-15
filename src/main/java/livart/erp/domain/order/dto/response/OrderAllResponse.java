package livart.erp.domain.order.dto.response;

import livart.common.dto.enums.order.DeliveryStatus;
import livart.common.dto.enums.order.PaymentMethod;
import livart.common.dto.enums.order.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderAllResponse {
    private Long orderId;
    private Long orderItemId;
    private Long userId;
    private String orderNum;
    private String orderName; // 주문자명
    private LocalDate orderDate;
    private String productName;
    private Set<OrderOptionResponse> orderOption;
    private BigDecimal finalPrice;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private DeliveryStatus deliveryStatus;

    private Boolean isExchanged;
    private Boolean isReturned;
    private Boolean isRefunded;

}
