package livart.erp.domain.member.dto.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import livart.common.dto.enums.coupon.CouponStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponUseLogResponse {
    private Long couponId;
    private CouponStatus status;
    private BigDecimal discountPrice;
    private String description;
    private LocalDateTime logTime;
}
