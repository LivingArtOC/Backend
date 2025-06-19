package livart.common.domain.portfolio.repository;

import livart.common.domain.portfolio.entity.Portfolio;
import livart.common.domain.portfolio.entity.PortfolioImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PortfolioImageRepository extends JpaRepository<PortfolioImage, Long> {
    List<PortfolioImage> findAllByPortfolio(Portfolio portfolio);

    @Modifying
    @Query("DELETE FROM PortfolioImage i WHERE i.portfolio.id = :portfolioId")
    void deleteImagesByPortfolioId(@Param("portfolioId") Long portfolioId);

}
