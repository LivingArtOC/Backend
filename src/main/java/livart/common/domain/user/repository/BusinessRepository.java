package livart.common.domain.user.repository;

import livart.common.domain.user.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long> {
    Optional<Business> findBusinessByBizRegistrationNum(String num);
}
