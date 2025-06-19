package livart.common.domain.user.repository;

import livart.common.domain.user.entity.RestrictIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestrictIpRepository extends JpaRepository<RestrictIp, Long> {

    @Query("""
    SELECT r FROM RestrictIp r 
    WHERE r.ipAddress = :clientIp 
    ORDER BY r.createdAt DESC
    """)
    List<RestrictIp> findTopByIpAddressOrderByCreatedAtDesc(@Param("clientIp") String clientIp);

    @Modifying
    void deleteAllByIpAddress(String clientIp);
}
