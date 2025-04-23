package livart.common.domain.term.repository;

import livart.common.domain.term.entity.DetailTerms;
import livart.common.domain.term.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DetailTermsRepository extends JpaRepository<DetailTerms, Long> {
    Optional<DetailTerms> findByTerms(Terms terms);

}
