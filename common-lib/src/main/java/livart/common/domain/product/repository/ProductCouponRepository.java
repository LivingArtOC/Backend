package livart.common.domain.product.repository;

import livart.common.domain.product.entity.Product;
import livart.common.domain.product.entity.ProductCoupon;
import livart.common.domain.promotion.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ProductCouponRepository extends JpaRepository<ProductCoupon, Long> {
    List<ProductCoupon> findAllByProduct(Product product);
    @Query("select pc.coupon.id from ProductCoupon pc where pc.product.id = :productId")
    List<Long> findCouponIdsByProductId(@Param("productId") Long productId);

    @Query("select pc.product from ProductCoupon pc where pc.coupon.id = :couponId")
    List<Product> findProductsByCouponId(@Param("couponId") Long couponId);
    @Modifying
    @Query("DELETE FROM ProductCoupon pc WHERE pc.product = :product")
    void deleteAllByProduct(@Param("product") Product product);

    @Modifying
    @Query("DELETE FROM ProductCoupon pc WHERE pc.coupon = :coupon")
    void deleteAllByCoupon(@Param("coupon") Coupon coupon);
}
