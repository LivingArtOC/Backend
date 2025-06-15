package livart.common.domain.alarm.repository;

import livart.common.domain.alarm.entity.EmailSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailSettingRepository extends JpaRepository<EmailSetting, Long> {
    Optional<EmailSetting> findFirstByIsActiveTrue();
}
