package livart.common.domain.support.notice.repository;

import livart.common.domain.support.notice.entity.Notice;
import livart.common.domain.support.notice.entity.NoticeImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeImageRepository extends JpaRepository<NoticeImage, Long> {
    void deleteAllByNotice(Notice notice);
}
