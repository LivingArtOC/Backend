package livart.common.domain.support.estimate.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.domain.product.entity.Option;
import livart.common.domain.product.entity.Product;
import livart.common.dto.enums.product.BrandType;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Table(name = "estimate_item")
@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EstimateItem extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private String optionCode;
    private String productName;
    private String thumbNailImgUrl;
    private String hashCode;

    @Enumerated(EnumType.STRING)
    private BrandType brand;

    private Integer quantity;

    private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estimate_id", nullable = false)
    private Estimate estimate;

    @Builder.Default
    @OneToMany(mappedBy = "estimateItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EstimateItemOption> estimateItemOptions = new ArrayList<>();
}
