package livart.common.log.repository;

import livart.common.log.entity.AdminActionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDateTime;

public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, Long> {

    @Query("""
    SELECT a FROM AdminActionLog a
    WHERE LOWER( a.adminLoginId) LIKE LOWER(CONCAT('%', :loginId, '%'))
      AND a.targetTable = :table
      AND a.createdAt BETWEEN :start AND :end
""")
    Page<AdminActionLog> findLogsByPartialLoginId(
            @Param("loginId") String loginId,
            @Param("table") String table,
            @Param("start") Instant start,
            @Param("end") Instant end,
            Pageable pageable
    );
}
