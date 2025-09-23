package livart.common.domain.order.repository;

import livart.common.domain.order.entity.Order;
import livart.common.domain.order.entity.Statement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StatementRepository extends JpaRepository<Statement, Long> {
    Optional<Statement> findByOrderId(Long orderId);
}
