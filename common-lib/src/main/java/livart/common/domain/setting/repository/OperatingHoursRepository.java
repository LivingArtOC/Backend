package livart.common.domain.setting.repository;

import livart.common.domain.setting.entity.OperatingHours;
import livart.common.dto.enums.defaultSetting.OperatingHoursType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperatingHoursRepository extends JpaRepository<OperatingHours, Long> {
    void deleteByOperatingHoursType(OperatingHoursType type);
    List<OperatingHours> findByOperatingHoursType(OperatingHoursType type);
}
