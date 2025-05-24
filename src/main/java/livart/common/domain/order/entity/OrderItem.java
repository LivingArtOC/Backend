package livart.common.domain.order.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.domain.product.entity.Option;
import livart.common.dto.enums.order.DeliveryStatus;
import livart.common.dto.enums.order.DepositStatus;
import livart.common.dto.enums.order.OrderStatus;
import livart.erp.domain.order.dto.request.OrderItemStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Table(name = "order_item")
@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private String productCode;
    private String productName;
    private String optionCode;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문됨, 주문 취소, 교환, 환불, 반품
    private Long couponId;

    @Column(precision = 10, scale = 2)
    private BigDecimal originalPrice; // 정가(소비자가) 찍찍이

    @Column(precision = 10, scale = 2)
    private BigDecimal salePrice; // 판매가

    @Column(precision = 10, scale = 2)
    private BigDecimal delPrice; // 납품가

    @Column(precision = 10, scale = 2)
    private BigDecimal finalPrice; // 쿠폰 적용 가격

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Builder.Default
    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AfterServiceRequest> afterServiceRequests = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemOption> orderItemOptions = new ArrayList<>();

    public void updateOrderStatus(OrderStatus status, Long updatedBy){
        this.orderStatus = status;
        this.updatedBy = updatedBy;
    }

    public void updateDelStatus(DeliveryStatus status, Long updatedBy){
        this.deliveryStatus = status;
        this.updatedBy = updatedBy;
    }
}
