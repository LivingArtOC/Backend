package livart.common.log.repository;

import livart.common.dto.enums.user.MileageType;
import livart.common.log.entity.MileageLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MileageLogRepository extends JpaRepository<MileageLog, Long> {
    List<MileageLog> findByUserIdAndTypeIn(Long userId, List<MileageType> types);
}
