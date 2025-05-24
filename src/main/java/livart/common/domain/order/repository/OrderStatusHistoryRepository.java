package livart.common.domain.order.repository;

import livart.common.domain.order.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
}
