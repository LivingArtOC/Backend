package livart.common.domain.promotion.repository;

import livart.common.domain.promotion.entity.CouponAutoGrant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponAutoGrantRepository extends JpaRepository<CouponAutoGrant, Long> {
}
