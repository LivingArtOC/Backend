package livart.common.domain.promotion.entity;


import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.domain.address.entity.UserAddress;
import livart.common.domain.member.entity.CouponLog;
import livart.common.domain.product.entity.ProductCoupon;
import livart.common.dto.enums.coupon.CouponDiscountType;
import livart.common.dto.enums.coupon.CouponExpiration;
import livart.common.dto.enums.coupon.CouponType;
import livart.common.dto.enums.coupon.IssuedMethod;
import livart.common.dto.enums.coupon.IssuedStatus;
import livart.common.dto.request.CouponRegisterRequest;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "coupon")
@Entity @Builder(toBuilder = true) @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Coupon extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    @Enumerated(EnumType.STRING)
    private IssuedMethod issuedMethod;

    @Enumerated(EnumType.STRING)
    private IssuedStatus issuedStatus;
    private String couponName;
    private String code;

    @Lob
    private String description;

    @Enumerated(EnumType.STRING)
    private CouponExpiration couponExpiration;
    private LocalDate expireStartDate;
    private LocalDate expireEndDate;
    private Long issuedDate;

    @Enumerated(EnumType.STRING)
    private CouponDiscountType couponDiscountType;

    private BigDecimal discountPrice;
    private Boolean expiredMessage; // 만료 발송 알림톡
    private Integer minOrderPrice;
    private Long createdBy;
    private Long updatedBy;

    @Builder.Default
    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CouponAutoGrant> couponAutoGrants = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CouponLog> couponLogs = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductCoupon> productCoupons = new ArrayList<>();

    public void update(CouponRegisterRequest request, Long updatedBy){
        this.couponType = request.getCouponType();
        this.issuedMethod = request.getIssuedMethod();
        this.issuedStatus = request.getIssuedStatus();
        this.couponName = request.getCouponName();
        this.code = request.getCode();
        this.description = request.getDescription();
        this.couponExpiration = request.getCouponExpiration();
        this.expireStartDate = request.getExpireStartDate();
        this.expireEndDate = request.getExpireEndDate();
        this.issuedDate = request.getIssuedDate();
        this.couponDiscountType = request.getCouponDiscountType();
        this.discountPrice = request.getDiscountPrice();
        this.expiredMessage = request.getExpiredMessage();
        this.minOrderPrice = request.getMinOrderPrice();
        this.updatedBy = updatedBy;
    }

    public void updateStatus(IssuedStatus status, Long updatedBy){
        this.issuedStatus = status;
        this.updatedBy = updatedBy;
    }
}
