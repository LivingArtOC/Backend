package livart.common.domain.term.repository;

import livart.common.domain.term.entity.UserTerms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTermsRepository extends JpaRepository<UserTerms, Long> {
}
