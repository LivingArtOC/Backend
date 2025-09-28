package livart.common.domain.order.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.order.ClaimReturnType;
import livart.common.dto.enums.order.OrderItemStatus;
import livart.common.dto.enums.order.OrderStatus;
import livart.common.dto.enums.order.RequestStatus;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Lob
    private String requestReason;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private LocalDateTime completeDate;

    private Long handledBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderItem_id", nullable = false)
    private OrderItem orderItem;

    @Builder.Default
    @OneToMany(mappedBy = "afterServiceRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ASImage> asImages = new ArrayList<>();

    public void changeStatus(RequestStatus status, Long handledBy){
        this.status = status;
        this.handledBy = handledBy;
    }

}
