package livart.erp.domain.promotion.dto.response;

import livart.common.dto.enums.coupon.TriggerEvents;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponAutoResponse {
    private TriggerEvents triggerEvents;
    private Boolean enabled;
    private List<AutoCouponList> couponLists;
}
