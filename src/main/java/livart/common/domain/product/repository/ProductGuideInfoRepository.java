package livart.common.domain.product.repository;

import livart.common.domain.product.entity.Product;
import livart.common.domain.product.entity.ProductGuideInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface ProductGuideInfoRepository extends JpaRepository<ProductGuideInfo, Long> {

    List<ProductGuideInfo> findByProduct(Product product);

    @Modifying
    void deleteAllByProduct(Product product);

    @Modifying
    void deleteAllByProductIdIn(List<Long> idList);
}
