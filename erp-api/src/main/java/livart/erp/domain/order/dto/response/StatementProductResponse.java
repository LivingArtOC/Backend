package livart.erp.domain.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatementProductResponse {
    private Long itemId;
    private Long productId;
    private String productName;
    private String productCode;
    private String productSize;
    private Integer quantity;
    private BigDecimal unitOriginalPrice;
    private BigDecimal unitSalePrice; // 판매가 (견적서 상에서 납품가 단가)
    private BigDecimal salePriceVat; // 공급가
    private String note;
}
