package livart.common.domain.product.repository;

import livart.common.domain.product.entity.CategoryDisplay;
import livart.common.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryDisplayRepository extends JpaRepository<CategoryDisplay, Long> {

    @Modifying
    void deleteAllByCategoryId(Long categoryId);

    List<CategoryDisplay> findByProduct(Product product);

    @Modifying
    void deleteAllByProductIdIn(List<Long> idList);

}
