package livart.common.domain.support.quotation.repository;

import livart.common.domain.support.quotation.entity.Quotation;
import livart.common.domain.support.quotation.entity.QuotationItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuotationItemRepository extends JpaRepository <QuotationItem, Long> {

    @EntityGraph(attributePaths = "quotationItemOptions")
    List<QuotationItem> findAllByQuotation(Quotation quotation);
}
