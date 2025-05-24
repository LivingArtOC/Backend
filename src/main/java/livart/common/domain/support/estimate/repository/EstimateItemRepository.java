package livart.common.domain.support.estimate.repository;

import livart.common.domain.support.estimate.entity.Estimate;
import livart.common.domain.support.estimate.entity.EstimateItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface EstimateItemRepository extends JpaRepository<EstimateItem, Long> {
    List<EstimateItem> findByEstimate(Estimate estimate);

    @Modifying
    void deleteAllByEstimateId(Long estimateId);
}
