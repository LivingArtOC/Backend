package livart.common.domain.product.repository;

import livart.common.domain.product.entity.Product;
import livart.common.domain.product.entity.ProductImage;
import livart.common.dto.enums.product.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProduct(Product product);
    @Modifying
    void deleteAllByProduct(Product product);

    @Modifying
    void deleteAllByProductIdIn(List<Long> IdList);
    ProductImage findByProductAndImageType(Product product, ImageType type);
}
