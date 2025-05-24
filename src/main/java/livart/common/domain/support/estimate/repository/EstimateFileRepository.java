package livart.common.domain.support.estimate.repository;

import livart.common.domain.support.estimate.entity.Estimate;
import livart.common.domain.support.estimate.entity.EstimateFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstimateFileRepository extends JpaRepository<EstimateFile, Long> {
    List<EstimateFile> findByEstimateId(Long estimateId);
}
