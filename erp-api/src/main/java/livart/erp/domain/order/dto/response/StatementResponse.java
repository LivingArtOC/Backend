package livart.erp.domain.order.dto.response;

import livart.erp.domain.support.quotation.dto.response.QuotationProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatementResponse {
    private Long orderId;
    private LocalDate orderDate;
    private String bizNum;
    private String address;
    private String phoneNum;
    private BigDecimal totalPriceExclVat; // 부가세 별도 총 금액
    private BigDecimal totalPriceInclVat; // 부가세 포함 총 금액
    private Integer quantitySubtotal; // 수량 소계
    private BigDecimal unitSubtotalVat; // 단가 소계
    private BigDecimal priceSubtotal; // 공급가액(VAT 포함) 소계
    private BigDecimal totalDiscount; // 할인 금액
    private BigDecimal unitTruncation; // 단위 절사
    private List<StatementProductResponse> productList;
}
