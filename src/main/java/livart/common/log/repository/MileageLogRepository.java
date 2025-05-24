package livart.common.log.repository;

import livart.common.log.entity.MileageLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MileageLogRepository extends JpaRepository<MileageLog, Long> {
}
