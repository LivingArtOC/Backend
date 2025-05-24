package livart.common.domain.term.repository;

import livart.common.domain.term.entity.DetailTerm;
import livart.common.domain.term.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DetailTermRepository extends JpaRepository<DetailTerm, Long> {
    Optional<DetailTerm> findByTerm(Term term);

}
