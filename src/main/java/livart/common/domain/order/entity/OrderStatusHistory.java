package livart.common.domain.order.entity;

import jakarta.persistence.*;
import livart.common.dto.enums.order.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "order_status_history")
@Builder @Entity @Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String memo; // 변경 사유, 관리자 입력 메시지

    private LocalDateTime changedAt;
    private Long changedBy;
}
