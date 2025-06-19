package livart.common.domain.term.repository;

import livart.common.domain.term.entity.Term;
import livart.common.dto.enums.term.TermSuperType;
import livart.common.dto.enums.term.TermType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {
    Optional<Term> findByType(TermType type);
    List<Term> findBySuperType(TermSuperType type);

    Optional<Term> findByTypeAndIsRequired(TermType type, Boolean isRequired);
    @Query("SELECT t FROM Term t WHERE t.type NOT IN :excludedTypes")
    List<Term> findAllByTypeNotIn(@Param("excludedTypes") List<TermType> excludedTypes);
}
