package livart.common.domain.promotion.repository;

import livart.common.domain.promotion.entity.CouponAutoSetting;
import livart.common.dto.enums.coupon.TriggerEvents;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CouponAutoSettingRepository extends JpaRepository<CouponAutoSetting, Long> {
    Optional<CouponAutoSetting> findByTriggerEvents(TriggerEvents triggerEvents);

    @EntityGraph(attributePaths = {"couponAutoGrants", "couponAutoGrants.coupon"})
    @Query("SELECT s FROM CouponAutoSetting s")
    List<CouponAutoSetting> findAllWithGraph();
}
