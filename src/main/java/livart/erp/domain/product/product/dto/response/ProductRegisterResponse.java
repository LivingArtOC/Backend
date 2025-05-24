package livart.erp.domain.product.product.dto.response;

import livart.common.dto.enums.coupon.CouponDiscountType;
import livart.common.dto.enums.product.BrandType;
import livart.common.dto.enums.product.DeliveryType;
import livart.common.dto.enums.product.StockStatus;
import livart.erp.domain.product.option.DetailedOptionResponse;
import livart.erp.domain.product.option.OptionCombinationResponse;
import livart.erp.domain.product.option.OptionResponse;
import livart.erp.domain.product.productColor.ProductColorResponse;
import livart.erp.domain.product.productGuide.ProductGuideInfoResponse;
import livart.erp.domain.product.productImage.ProductImageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRegisterResponse {
    private Long productId;
    private Long categoryId;
    private String productName;
    private String productCode;
    private String keyword;
    private List<ProductColorResponse> productColors;
    private BrandType brand;
    private StockStatus status;
    private Boolean restockAlert;
    private CouponDiscountType mileageType;
    private BigDecimal mileageRate;
    private BigDecimal delPrice;
    private BigDecimal originalPrice;
    private BigDecimal salePrice;
    private String replaceComment;
    private Boolean optionUsage;
    private List<DetailedOptionResponse> detailedOptions;
    private List<OptionCombinationResponse> optionCombinations;
    private List<ProductImageResponse> productImages;
    private DeliveryType deliveryType;
    private String deliveryText;
    private Integer deliveryPrice;
    private List<ProductGuideInfoResponse> productGuideInfos;
    private LocalDateTime createdAt;
}
