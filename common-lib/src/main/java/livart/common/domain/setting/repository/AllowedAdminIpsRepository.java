package livart.common.domain.setting.repository;

import livart.common.domain.setting.entity.AllowedAdminIp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AllowedAdminIpsRepository extends JpaRepository<AllowedAdminIp, Long> {
    List<AllowedAdminIp> findByAdminId(Long adminId);
    void deleteALLByAdminId(Long adminId);
}
