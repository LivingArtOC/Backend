package livart.erp.domain.product.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDisplayResponse {
    private Long productId;
    private Integer manualOrder;
    private String imageUrl;
    private String productName;
    private BigDecimal salePrice;
    private Boolean isPinned;
    private LocalDateTime createdAt;
}
