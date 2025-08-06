package livart.common.domain.order.repository;

import livart.common.domain.order.entity.OrderClaim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderClaimRepository extends JpaRepository<OrderClaim, Long> {
}
