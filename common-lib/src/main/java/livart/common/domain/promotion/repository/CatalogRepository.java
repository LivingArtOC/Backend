package livart.common.domain.promotion.repository;

import livart.common.domain.promotion.entity.Catalog;
import livart.common.dto.enums.design.CatalogType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CatalogRepository extends JpaRepository<Catalog, Long> {
    Optional<Catalog> findCatalogByCatalogType(CatalogType catalogType);
}
