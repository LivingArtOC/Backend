package livart.common.domain.wishlist.repository;

import livart.common.domain.wishlist.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    void deleteByUserIdAndProductId(Long userId, Long productId);

    long countByUserId(Long userId);

    @Query("select max(w.id) from Wishlist w where w.userId = :userId")
    Long findLastRowIdByUserId(Long userId); // ETag 보강 등에 활용 가능(옵션)
}