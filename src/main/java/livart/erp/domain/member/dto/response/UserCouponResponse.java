package livart.erp.domain.member.dto.response;

import livart.common.dto.enums.coupon.CouponDiscountType;
import livart.common.dto.enums.coupon.CouponExpiration;
import livart.common.dto.enums.coupon.CouponType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCouponResponse {
    private Long couponId;
    private Boolean isExpired;
    private CouponType couponType;
    private String couponName;
    private CouponExpiration couponExpiration;
    private LocalDate expireEndDate;
    private Integer issuedDate;
    private CouponDiscountType couponDiscountType;
    private BigDecimal discountPrice;
}
