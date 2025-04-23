package livart.common.domain.promotion.repository;

import livart.common.domain.promotion.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
