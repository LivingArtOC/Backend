package livart.erp.domain.order.dto.response;

import livart.common.dto.enums.order.DeliveryStatus;
import livart.common.dto.enums.order.PaymentMethod;
import livart.common.dto.enums.order.PaymentStatus;
import livart.common.dto.enums.product.BrandType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoResponse {
    private Long orderId;
    private Long orderItemId;
    private String orderNum;
    private String orderName; // 주문자명
    private LocalDate orderDate;
    private String productName;
    private Long productId;
    private BrandType brand;
    private List<OrderOpInfoResponse> orderOption;
}
