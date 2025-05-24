package livart.erp.domain.promotion.dto.response;

import livart.common.dto.enums.coupon.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CouponRegisterResponse {
    private Long couponId;
    private CouponType couponType;
    private IssuedMethod issuedMethod;
    private IssuedStatus issuedStatus;
    private String couponName;
    private String code;
    private String description;
    private CouponExpiration couponExpiration;
    private LocalDate expireStartDate;
    private LocalDate expireEndDate;
    private Long issuedDate;
    private CouponDiscountType couponDiscountType;
    private Long discountPrice;
    private Boolean expiredMessage;
    private Integer minOrderPrice;
}
