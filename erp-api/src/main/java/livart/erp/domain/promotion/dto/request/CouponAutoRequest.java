package livart.erp.domain.promotion.dto.request;

import livart.common.dto.enums.coupon.TriggerEvents;
import livart.erp.domain.promotion.dto.response.AutoCouponList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class CouponAutoRequest {
    private TriggerEvents triggerEvents;
    private Boolean enabled;
    private List<Long> couponIdList;
}
