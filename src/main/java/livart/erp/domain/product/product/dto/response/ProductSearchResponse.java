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
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchResponse {
    private Long productId;
    private String productCode;
    private String imageUrl;
    private String productName;
    private StockStatus status;
    private BigDecimal salePrice;
    private CouponDiscountType type;
    private BigDecimal mileageRate;
    private BigDecimal originalPrice;

    @QueryProjection
    public ProductSearchResponse(Long productId, String productCode, String productName, StockStatus status,
                                 String imageUrl, BigDecimal salePrice, BigDecimal originalPrice, CouponDiscountType type, BigDecimal mileageRate) {
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.status = status;
        this.imageUrl = imageUrl;
        this.salePrice = salePrice;
        this.originalPrice = originalPrice;
        this.type = type;
        this.mileageRate = mileageRate;
    }
}
