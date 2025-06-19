package livart.erp.domain.promotion.dto.response;

import livart.common.dto.enums.coupon.CouponType;
import livart.common.dto.enums.coupon.IssuedMethod;
import livart.common.dto.enums.coupon.IssuedStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponSearchResponse {
    private Long couponId;
    private String couponName;
    private LocalDate registeredAt;
    private CouponType couponType;
    private String expireDate;
    private String discountRate;
    private IssuedMethod issuedMethod;
    private IssuedStatus issuedStatus;
    private String description;
}
