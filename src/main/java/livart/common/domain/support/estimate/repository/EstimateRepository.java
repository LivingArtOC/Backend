package livart.common.domain.support.estimate.repository;

import livart.common.domain.support.estimate.entity.Estimate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstimateRepository extends JpaRepository<Estimate, Long> {
}
