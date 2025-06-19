package livart.common.domain.member.repository;

import livart.common.domain.member.entity.CouponLog;
import livart.common.dto.enums.coupon.CouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponLogRepository extends JpaRepository<CouponLog, Long> {
    List<CouponLog> findAllByUserIdAndStatusIn(Long userId, List<CouponStatus> statuses);
}
