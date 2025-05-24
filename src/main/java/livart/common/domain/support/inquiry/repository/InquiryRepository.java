package livart.common.domain.support.inquiry.repository;

import livart.common.domain.support.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry,Long> {
}
