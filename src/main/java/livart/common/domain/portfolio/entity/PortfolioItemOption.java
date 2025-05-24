package livart.common.domain.portfolio.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.domain.product.entity.DetailedOption;
import lombok.*;

@Table(name = "portfolio_item_option")
@Entity @Builder @Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortfolioItemOption extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String optionName; // 옵션 명
    private String valueName; // 옵션 값
    private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolioItem_id", nullable = false)
    private PortfolioItem portfolioItem;
}
