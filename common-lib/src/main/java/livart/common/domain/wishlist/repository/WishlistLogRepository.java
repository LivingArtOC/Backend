package livart.common.domain.wishlist.repository;

import livart.common.domain.wishlist.entity.WishlistLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistLogRepository extends JpaRepository<WishlistLog, Long> {
}