package livart.erp.domain.support.quotation.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuotationProductResponse {
    private Long itemId;
    private Long productId;
    private String productName;
    private String productCode;
    private String hashCode;
    private String productSize;
    private String thumbNailImgUrl;
    private Integer quantity;
    private BigDecimal unitOriginalPrice;
    private BigDecimal unitSalePrice; // 판매가 (견적서 상에서 납품가 단가)
    private BigDecimal salePriceVat; // 공급가
    private String message;
    private String note; // 비고
    private List<QuotationOptionResponse> options;
}
