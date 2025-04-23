package livart.common.domain.term.repository;

import livart.common.domain.term.entity.Terms;
import livart.common.dto.enums.TermType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TermsRepository extends JpaRepository<Terms, Long> {
    Optional<Terms> findByTitle(String title);
    @Query("SELECT t FROM Terms t WHERE t.type NOT IN :excludedTypes")
    List<Terms> findAllByTypeNotIn(@Param("excludedTypes") List<TermType> excludedTypes);
}
