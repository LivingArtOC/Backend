package livart.common.domain.support.quotation.repository;


import livart.common.domain.support.quotation.entity.Quotation;
import livart.common.domain.support.quotation.entity.QuotationItemOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuotationItemOptionRepository extends JpaRepository<QuotationItemOption, Long> {
}
