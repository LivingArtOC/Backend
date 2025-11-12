package livart.common.domain.wishlist.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "wishlist_logs")
public class WishlistLog {

    public enum Action { ADDED, REMOVED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK만 보유
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 16)
    private Action action;

    // DB DEFAULT CURRENT_TIMESTAMP 사용
    @Column(name = "action_at", nullable = false, updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime actionAt;

    public static WishlistLog of(Long userId, Long productId, Action action) {
        return WishlistLog.builder()
                .userId(userId)
                .productId(productId)
                .action(action)
                .build();
    }
}