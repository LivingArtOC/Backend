package livart.common.domain.product.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.domain.support.estimate.entity.EstimateItem;
import livart.common.dto.enums.coupon.CouponDiscountType;
import livart.common.dto.enums.product.BrandType;
import livart.common.dto.enums.product.DeliveryType;
import livart.common.dto.enums.product.ProductStatus;
import livart.common.dto.enums.product.StockStatus;
import livart.erp.domain.product.product.dto.request.ProductRegisterRequest;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Table(name = "product")
@Entity @Builder @Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long categoryId;
    private String productCode;
    private String productName;
    private String keyword;

    @Enumerated(EnumType.STRING)
    private BrandType brand;

    @Enumerated(EnumType.STRING)
    private StockStatus status;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus = ProductStatus.ACTIVE;

    private Boolean restockAlert;

    @Enumerated(EnumType.STRING)
    private CouponDiscountType mileageType;
    private BigDecimal mileageRate;

    @Column(precision = 10, scale = 2)
    private BigDecimal originalPrice; // 정가(소비자가) 찍찍이

    @Column(precision = 10, scale = 2)
    private BigDecimal salePrice; // 판매가

    @Column(precision = 10, scale = 2)
    private BigDecimal delPrice; // 납품가

    private String replaceComment; // 가격 대체 문구

    private Boolean optionUsage;

    @Setter
    @Builder.Default
    private Boolean isPinned = false;

    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;
    private String deliveryText;
    private Integer deliveryPrice;

    private Long createdBy;
    private Long updatedBy;

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductColor> productColors = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductGuideInfo> productGuideInfos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryDisplay> categoryDisplays = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailedOption> detailedOptions = new ArrayList<>();

    public void updateStock(StockStatus status, Long updatedBy){
        this.status = status;
        this.updatedBy = updatedBy;
    }

    public void deactivate(ProductStatus status, Long updatedBy) {
        this.productStatus = status;
        this.updatedBy = updatedBy;
    }

    public void changePrice(BigDecimal originalPrice, Long updatedBy) {
        this.originalPrice = originalPrice;
        this.updatedBy = updatedBy;
    }

    public void update(ProductRegisterRequest request, Long updatedBy){
        this.categoryId = request.getCategoryId();
        this.productName = request.getProductName();
        this.productCode = request.getProductCode();
        this.keyword = request.getKeyword();
        this.brand = request.getBrand();
        this.status = request.getStatus();
        this.restockAlert = request.getRestockAlert();
        this.mileageType = request.getMileageType();
        this.mileageRate = request.getMileageRate();
        this.originalPrice = request.getOriginalPrice();
        this.delPrice = request.getDelPrice();
        this.salePrice = request.getSalePrice();
        this.replaceComment = request.getReplaceComment();
        this.optionUsage = request.getOptionUsage();
        this.deliveryPrice = request.getDeliveryPrice();
        this.deliveryText = request.getDeliveryText();
        this.deliveryType = request.getDeliveryType();
        this.updatedBy = updatedBy;
    }

}
