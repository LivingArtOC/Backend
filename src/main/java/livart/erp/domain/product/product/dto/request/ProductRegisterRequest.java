package livart.erp.domain.product.product.dto.request;

import livart.common.dto.enums.coupon.CouponDiscountType;
import livart.common.dto.enums.product.BrandType;
import livart.common.dto.enums.product.DeliveryType;
import livart.common.dto.enums.product.StockStatus;
import livart.erp.domain.product.option.DetailedOptionRequest;
import livart.erp.domain.product.option.OptionCombinationRequest;
import livart.erp.domain.product.option.OptionRequest;
import livart.erp.domain.product.productColor.ProductColorRequest;
import livart.erp.domain.product.productGuide.ProductGuideInfoRequest;
import livart.erp.domain.product.productImage.ProductImageRequest;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class ProductRegisterRequest {
    private Long categoryId;
    private String productName;
    private String productCode;
    private String keyword;
    private List<ProductColorRequest> productColors;
    private BrandType brand;
    private StockStatus status;
    private Boolean restockAlert;
    private CouponDiscountType mileageType;
    private BigDecimal mileageRate;
    private BigDecimal delPrice; // 납품가
    private BigDecimal originalPrice;
    private BigDecimal salePrice;
    private String replaceComment;
    private Boolean optionUsage;
    private List<DetailedOptionRequest> detailedOptions;
    private List<OptionCombinationRequest> optionCombinations;
    private List<ProductImageRequest> productImageList;
    private DeliveryType deliveryType;
    private String deliveryText;
    private Integer deliveryPrice;
    private List<ProductGuideInfoRequest> productGuideInfos;
}

