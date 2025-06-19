package livart.common.domain.portfolio.repository;

import livart.common.domain.portfolio.entity.Portfolio;
import livart.common.domain.portfolio.entity.PortfolioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {

    List<PortfolioItem> findAllByPortfolio(Portfolio portfolio);

    @Modifying
    @Query("DELETE FROM PortfolioItem i WHERE i.portfolio.id = :portfolioId")
    void deleteItemsByPortfolioId(@Param("portfolioId") Long portfolioId);

}
