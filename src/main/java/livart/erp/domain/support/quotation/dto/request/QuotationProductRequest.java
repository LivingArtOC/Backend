package livart.erp.domain.support.quotation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class QuotationProductRequest {
    private Long productId;
    private String productName;
    private String hashCode;
    private String productCode;
    private String productSize;
    private String thumbNailImgUrl;
    private Integer quantity;
    private BigDecimal unitOriginalPrice;
    private BigDecimal unitSalePrice; // 판매가 (견적서 상에서 납품가 단가)
    private BigDecimal salePriceVat; // 공급가
    private String note; // 비고
    private List<QuotationOptionRequest> options;
}
