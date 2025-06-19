package livart.common.log.repository;

import livart.common.log.entity.PasswordLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordLogRepository extends JpaRepository<PasswordLog, Long> {
}
