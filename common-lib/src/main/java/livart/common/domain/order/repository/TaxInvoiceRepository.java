package livart.common.domain.order.repository;

import livart.common.domain.order.entity.TaxInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxInvoiceRepository extends JpaRepository<TaxInvoice, Long> {
}
