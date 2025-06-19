package livart.common.dto.request;

import livart.common.dto.enums.coupon.CouponDiscountType;
import livart.common.dto.enums.product.BrandType;
import livart.common.dto.enums.product.DeliveryType;
import livart.common.dto.enums.product.StockStatus;
import livart.common.dto.request.product.*;
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

