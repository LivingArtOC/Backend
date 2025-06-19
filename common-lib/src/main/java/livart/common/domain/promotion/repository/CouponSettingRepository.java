package livart.common.domain.promotion.repository;

import livart.common.domain.promotion.entity.CouponSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponSettingRepository extends JpaRepository<CouponSetting, Long> {
}
