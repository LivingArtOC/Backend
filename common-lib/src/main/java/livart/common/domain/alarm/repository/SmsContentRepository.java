package livart.common.domain.alarm.repository;

import livart.common.domain.alarm.entity.SmsContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsContentRepository extends JpaRepository<SmsContent, Long> {
}
