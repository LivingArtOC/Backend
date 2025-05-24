package livart.erp.domain.promotion.dto.request;

import jakarta.persistence.Lob;
import livart.common.dto.enums.coupon.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class CouponRegisterRequest {
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
