package livart.common.domain.portfolio.repository;

import livart.common.domain.portfolio.entity.Portfolio;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findAllByIdInAndIsPinnedFalse(List<Long> idList);

    Optional<Portfolio> findByOrderId(Long orderId);
}
