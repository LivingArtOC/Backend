package livart.common.domain.promotion.repository;

import livart.common.domain.promotion.entity.Coupon;
import livart.common.dto.enums.coupon.CouponIssuedSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findAllByIdIn(Set<Long> idList);

}
