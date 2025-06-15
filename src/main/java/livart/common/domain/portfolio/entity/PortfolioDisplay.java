package livart.common.domain.portfolio.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import lombok.*;

@Table(name = "portfolio_display")
@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PortfolioDisplay extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer orderIndex;
    private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;
}
