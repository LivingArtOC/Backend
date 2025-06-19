package livart.common.domain.support.faq.repository;

import livart.common.domain.support.faq.entity.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FAQRepository extends JpaRepository<FAQ, Long> {
}
