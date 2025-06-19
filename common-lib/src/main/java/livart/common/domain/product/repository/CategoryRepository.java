package livart.common.domain.product.repository;

import livart.common.domain.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByParentAndDepth(Category category, Integer depth);

    List<Category> findByParent(Category parent);
}
