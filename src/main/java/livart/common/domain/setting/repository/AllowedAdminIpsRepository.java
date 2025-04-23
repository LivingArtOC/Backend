package livart.common.domain.setting.repository;

import livart.common.domain.setting.entity.AllowedAdminIps;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AllowedAdminIpsRepository extends JpaRepository<AllowedAdminIps, Long> {
    List<AllowedAdminIps> findByAdminId(Long adminId);
    void deleteByAdminId(Long adminId);


}
