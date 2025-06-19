package livart.common.log.repository;

import org.springframework.data.domain.Page;
import livart.common.log.entity.LoginHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    @Query("""
    SELECT l FROM LoginHistory l
    WHERE LOWER(l.loginId) LIKE LOWER(CONCAT('%', :loginId, '%'))
      AND l.success = :success
      AND l.createdAt BETWEEN :start AND :end
""")
    Page<LoginHistory> findByLoginIdLikeAndSuccessAndLoginAtBetween(
            @Param("loginId") String loginId,
            @Param("success") boolean success,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    @Query("""
    SELECT l FROM LoginHistory l 
    WHERE l.ipAddress = :clientIp 
        AND l.createdAt >= :thresholdTime
        AND l.success = false 
    """)
    List<LoginHistory> findRecentByIpAddress(@Param("clientIp") String clientIp, @Param("thresholdTime") LocalDateTime thresholdTime);

}
