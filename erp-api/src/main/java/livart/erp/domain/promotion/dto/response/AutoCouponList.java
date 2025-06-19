package livart.erp.domain.promotion.dto.response;

import kotlinx.datetime.LocalDate;
import livart.common.dto.enums.coupon.TriggerEvents;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class AutoCouponList {
    private Long couponId;
    private String couponName;
    private String code;
    private String expireDate;
    private String discountRate;
    private String memo;
}
