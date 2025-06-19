package livart.erp.domain.product.product.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import livart.common.domain.product.entity.ProductImage;
import livart.common.dto.enums.coupon.CouponDiscountType;
import livart.common.dto.enums.product.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchResponse {
    private Long productId;
    private Long optionId;
    private String optionCode;
    private String imageUrl;
    private String productName;
    private StockStatus status;
    private BigDecimal salePrice;
    private BigDecimal originalPrice;
    private LocalDateTime registerAt;
    private LocalDateTime updatedAt;
    private String discountRate;
}
