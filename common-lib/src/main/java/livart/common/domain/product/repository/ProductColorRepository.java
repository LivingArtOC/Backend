package livart.common.domain.product.repository;

import livart.common.domain.product.entity.Product;
import livart.common.domain.product.entity.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface ProductColorRepository extends JpaRepository<ProductColor, Long> {

    List<ProductColor> findByProduct(Product product);

    @Modifying
    void deleteAllByProduct(Product product);

    @Modifying
    void deleteAllByProductIdIn(List<Long> idList);
}
