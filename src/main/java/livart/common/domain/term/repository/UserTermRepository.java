package livart.common.domain.term.repository;

import livart.common.domain.term.entity.UserTerm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTermRepository extends JpaRepository<UserTerm, Long> {
}
