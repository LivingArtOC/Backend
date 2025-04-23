package livart.common.log.repository;

import org.springframework.data.domain.Page;
import livart.common.log.entity.LoginHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDateTime;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    @Query("""
    SELECT l FROM LoginHistory l
    WHERE LOWER(l.loginId) LIKE LOWER(CONCAT('%', :loginId, '%'))
      AND l.success = :success
      AND l.attemptedAt BETWEEN :start AND :end
""")
    Page<LoginHistory> findByLoginIdLikeAndSuccessAndLoginAtBetween(
            @Param("loginId") String loginId,
            @Param("success") boolean success,
            @Param("start") Instant start,
            @Param("end") Instant end,
            Pageable pageable
    );

}
