package livart.common.domain.order.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.order.DepositStatus;
import livart.common.dto.enums.order.PaymentMethod;
import livart.common.dto.enums.order.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "payment")
@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String depositor; // 입금자
    private BigDecimal paidAmount;
    private String account;

    @Column(unique = true)
    private Long transactionId;

    private String bankName; // 계좌이체, 가상계좌일때만

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus; // PG사 기준

    @Enumerated(EnumType.STRING)
    private DepositStatus depositStatus; // 회사 입금 확인 기준
    private Long updatedBy;

    private LocalDateTime paidDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;


}
