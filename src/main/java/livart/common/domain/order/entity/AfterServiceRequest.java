package livart.common.domain.order.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.order.OrderStatus;
import livart.common.dto.enums.order.RequestStatus;
import livart.erp.domain.order.dto.request.OrderItemStatus;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "after_service_request")
@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AfterServiceRequest extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus requestType;

    private String requestReason;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus returnType; // 반품된다면 그 이후 결과

    private Long handledBy;

    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderItem_id", nullable = false)
    private OrderItem orderItem;

}
