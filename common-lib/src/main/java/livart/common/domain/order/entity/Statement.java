package livart.common.domain.order.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import lombok.*;

@Table(name = "statement")
@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Statement extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String deliveryDate;

    @Builder.Default
    private String accountNum = "1005-103-498953 우리은행";
    private Long handledBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public void update(String deliveryDate, String accountNum, Long handledBy){
        this.accountNum = accountNum;
        this.deliveryDate = deliveryDate;
        this.handledBy = handledBy;
    }
}
