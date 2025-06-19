package livart.common.log.repository;

import livart.common.log.entity.KakaoLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaoLogRepository extends JpaRepository<KakaoLog, Long> {
}
