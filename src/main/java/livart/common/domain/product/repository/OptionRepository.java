package livart.common.domain.product.repository;

import livart.common.domain.product.entity.Option;
import livart.common.domain.product.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OptionRepository extends JpaRepository<Option, Long> {
    @Query("SELECT o FROM Option o WHERE o.product.id = :productId")
    @EntityGraph(attributePaths = {"optionMappings", "optionMappings.detailedOption"})
    List<Option> findAllWithMappingsByProductId(@Param("productId") Long productId);

    List<Option> findAllByProductId(Long productId);

    Optional<Option> findByProductIdAndHashCode(Long productId, String hashCode);

    List<Option> findAllByProduct(Product product);
}
