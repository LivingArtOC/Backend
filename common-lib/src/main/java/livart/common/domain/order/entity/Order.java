package livart.common.domain.order.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.domain.portfolio.entity.Portfolio;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "orders")
@Builder @Entity @Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long quotationId;
    private Long userId;
    private String orderNum;
    private String orderName; // 주문자명
    private String orderEmail; // 주문자 이메일
    private String orderPhoneNum; // 주문자 전화번호

    @Column(precision = 10, scale = 2)
    private BigDecimal usedMileage; // 사용 마일리지

    @Column(precision = 10, scale = 2)
    private BigDecimal totalItemPrice; // 상품 판매가(쿠폰 적용 금액의 총합)

    @Column(precision = 10, scale = 2)
    private BigDecimal finalPaidAmount; // 최종 결제 금액

    private LocalDateTime orderDate;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

}
