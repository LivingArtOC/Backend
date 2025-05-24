package livart.common.domain.support.notice.repository;

import livart.common.domain.support.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
