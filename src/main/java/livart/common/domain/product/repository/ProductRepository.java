package livart.common.domain.product.repository;

import livart.common.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
    SELECT p FROM Product p
    WHERE p.categoryId = :categoryId
    AND p.brand = :brandType
    AND p.salePrice BETWEEN :minPrice AND :maxPrice
    AND p.createdAt BETWEEN :startDate AND :endDate
""")
    List<Product> findProductByNoSearchKey(@Param("categoryId") Long categoryId,
                                           @Param("brandType") String brandType,
                                           @Param("minPrice") BigDecimal minPrice,
                                           @Param("maxPrice") BigDecimal maxPrice,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    List<Product> findAllByIdIn(List<Long> idList);
}
