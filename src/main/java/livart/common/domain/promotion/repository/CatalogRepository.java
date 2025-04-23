package livart.common.domain.promotion.repository;

import io.swagger.v3.oas.annotations.Operation;
import livart.common.domain.promotion.entity.Catalog;
import livart.common.dto.enums.CatalogType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CatalogRepository extends JpaRepository<Catalog, Long> {
    Optional<Catalog> findCatalogByCatalogType(CatalogType catalogType);
}
