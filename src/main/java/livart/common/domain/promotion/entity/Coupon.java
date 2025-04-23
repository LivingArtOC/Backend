package livart.common.domain.promotion.entity;


import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import lombok.*;

@Table(name = "coupon")
@Entity @Builder @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Coupon extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


}
