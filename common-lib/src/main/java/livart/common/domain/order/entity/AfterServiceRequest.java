package livart.common.domain.order.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.order.OrderItemStatus;
import livart.common.dto.enums.order.OrderStatus;
import livart.common.dto.enums.order.RequestStatus;

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

    private String fileUrl;
    private String fileName;

    @Lob
    private String requestReason;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private LocalDateTime completeDate;

    private Long handledBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderItem_id", nullable = false)
    private OrderItem orderItem;

    public void changeStatus(RequestStatus status, Long handledBy){
        this.status = status;
        this.handledBy = handledBy;
    }

}
