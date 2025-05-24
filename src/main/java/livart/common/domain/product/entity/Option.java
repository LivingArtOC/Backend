package livart.common.domain.product.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.domain.order.entity.OrderItem;
import livart.common.domain.support.estimate.entity.EstimateItem;
import livart.common.dto.enums.product.StockStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Table(name = "product_option")
@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Option extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String optionCode;
    private Boolean isExposed;

    @Enumerated(EnumType.STRING)
    private StockStatus status;

    @Column(precision = 10, scale = 2)
    private BigDecimal purchasePrice; // 옵션 매입가

    @Column(precision = 10, scale = 2)
    private BigDecimal price; // 옵션 가격

    @Setter
    private String hashCode;

    private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder.Default
    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionMapping> optionMappings = new ArrayList<>();

}
