package livart.common.domain.order.repository;

import livart.common.domain.order.entity.AfterServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AfterServiceRequestRepository extends JpaRepository<AfterServiceRequest, Long> {
}
