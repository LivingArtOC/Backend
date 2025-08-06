package livart.common.domain.order.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.order.ClaimReason;
import livart.common.dto.enums.order.ClaimReqStatus;
import livart.common.dto.enums.order.RequestType;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "order_claim")
@Entity @Builder @Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderClaim extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    private ClaimReqStatus claimReqStatus;

    @Enumerated(EnumType.STRING)
    private ClaimReason reasonType;

    @Lob
    private String reason;
    private LocalDateTime completeDate;
    private Long handledBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderItem_id", nullable = false)
    private OrderItem orderItem;

    public void changeStatus(ClaimReqStatus status, Long handledBy){
        this.claimReqStatus = status;
        this.handledBy = handledBy;
    }
}
