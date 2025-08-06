package livart.common.domain.address.entity;


import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.domain.promotion.entity.Coupon;
import livart.common.domain.user.entity.User;
import lombok.*;

@Table(name = "user_address")
@Entity @Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAddress extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String recipient; // 받으실분
    private String phoneNum;

    @Column(nullable = false)
    private String zipcode;

    @Column(nullable = false)
    private String address;

    private String detailedAddress;

    @Column(nullable = false)
    private boolean defaultAddress;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
