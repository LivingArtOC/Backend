package livart.common.domain.promotion.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.coupon.TriggerEvents;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "coupon_auto_setting")
@Entity
@Builder @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponAutoSetting extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TriggerEvents triggerEvents;
    private Boolean enabled;

    private Long updatedBy;

    @Builder.Default
    @OneToMany(mappedBy = "couponAutoSetting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CouponAutoGrant> couponAutoGrants = new ArrayList<>();

    public void update(Boolean enabled, Long updatedBy){
        this.enabled = enabled;
        this.updatedBy = updatedBy;
    }
}
