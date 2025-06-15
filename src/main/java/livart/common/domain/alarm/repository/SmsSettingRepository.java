package livart.common.domain.alarm.repository;

import livart.common.domain.alarm.entity.SmsSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SmsSettingRepository extends JpaRepository<SmsSetting, Long> {
    Optional<SmsSetting> findFirstByIsActiveTrue();
}
