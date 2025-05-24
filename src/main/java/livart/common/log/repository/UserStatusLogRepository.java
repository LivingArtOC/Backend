package livart.common.log.repository;

import livart.common.log.entity.UserStatusLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusLogRepository extends JpaRepository<UserStatusLog, Long> {
}
