package livart.common.domain.support.notice.repository;

import livart.common.domain.support.notice.entity.Notice;
import livart.common.domain.support.notice.entity.NoticeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeImageRepository extends JpaRepository<NoticeImage, Long> {
    void deleteAllByNotice(Notice notice);

    @Modifying
    @Query("DELETE FROM NoticeImage nf WHERE nf.notice.id IN :ids")
    void deleteAllByNoticeIdIn(@Param("ids") List<Long> ids);
}
