package livart.common.domain.portfolio.repository;

import livart.common.domain.portfolio.entity.PortfolioItem;
import livart.common.domain.portfolio.entity.PortfolioItemOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PortfolioItemOptionRepository extends JpaRepository<PortfolioItemOption, Long> {

    List<PortfolioItemOption> findByPortfolioItemIdIn(List<Long> idList);

    @Modifying
    @Query("DELETE FROM PortfolioItemOption o WHERE o.portfolioItem.id IN (" +
            "SELECT i.id FROM PortfolioItem i WHERE i.portfolio.id = :portfolioId)")
    void deleteOptionsByPortfolioId(@Param("portfolioId") Long portfolioId);
}
