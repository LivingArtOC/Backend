package livart.common.domain.portfolio.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.product.BrandType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "portfolio_item")
@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortfolioItem extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer orderIndex;
    private String productName;
    private String productImageUrl;

    @Enumerated(EnumType.STRING)
    private BrandType brandType;
    private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Builder.Default
    @OneToMany(mappedBy = "portfolioItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioItemOption> portfolioItemOptions = new ArrayList<>();
}
