package livart.common.domain.order.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.order.ClaimReason;
import livart.common.dto.enums.order.ClaimReqStatus;
import livart.common.dto.enums.order.ClaimReturnType;
import livart.common.dto.enums.order.RequestType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "order_claim")
@Entity @Builder @Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderClaim extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId; // 신청자

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

    private LocalDate returnDate; // 회수 예정일
    private String request; // 요청사항
    private String invoiceNum; // 송장번호

    @Enumerated(EnumType.STRING)
    private ClaimReturnType returnType; // 회수 상태

    @Column(precision = 10, scale = 2)
    private BigDecimal returnFee; // 반품비


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderItem_id", nullable = false)
    private OrderItem orderItem;

    public void changeStatus(ClaimReqStatus status, Long handledBy){
        this.claimReqStatus = status;
        this.handledBy = handledBy;
    }

    public void update(String invoiceNum, ClaimReturnType returnType, LocalDate returnDate, String request, BigDecimal returnFee, Long handledBy){
        this.invoiceNum = invoiceNum;
        this.returnType = returnType;
        this.returnDate = returnDate;
        this.request = request;
        this.returnFee = returnFee;
        this.handledBy = handledBy;
    }
}
