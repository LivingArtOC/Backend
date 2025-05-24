package livart.common.domain.support.estimate.repository;

import livart.common.domain.support.estimate.entity.EstimateItemOption;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EstimateItemOptionRepository extends JpaRepository<EstimateItemOption, Long> {

    @Modifying
    @Query("DELETE FROM EstimateItemOption e WHERE e.estimateItem.id IN (" +
            "SELECT i.id FROM EstimateItem i WHERE i.estimate.id = :estimateId)")
    void deleteOptionsByEstimateId(@Param("estimateId") Long estimateId);
}
