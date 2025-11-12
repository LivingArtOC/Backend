package livart.common.domain.wishlist.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "wishlist",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_wishlist_user_product", columnNames = {"user_id", "product_id"})
        }
)
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK만 보유(Shop/ERP 공용 + 의존 최소화)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    // DB DEFAULT CURRENT_TIMESTAMP 사용 (updatable=false)
    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public static Wishlist of(Long userId, Long productId) {
        return Wishlist.builder()
                .userId(userId)
                .productId(productId)
                .build();
    }
}