package livart.common.domain.mileage.repository;

import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaPackage;
import livart.common.domain.mileage.entity.MileageSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MileageSettingRepository extends JpaRepository<MileageSetting, Long> {
}
