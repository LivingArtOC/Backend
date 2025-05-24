package livart.erp.domain.support.quotation.dto.response;

import livart.common.dto.enums.quotation.QuotationStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuotationAllResponse {
    private Long quotationId;
    private Long estimateId;
    private String phoneNum;
    private String title;
    private String bizNum; // 공급자 정보
    private String corporationName;
    private String presidentName;
    private String address;
    private String detailAddress;
    private List<PicListResponse> picList;
    private QuotationStatus status;
    private String managerCompanyName; // 대상
    private String managerName; // 대상
    private String memo;
    private LocalDate date;
    private BigDecimal totalPriceExclVat; // 부가세 별도 총 금액
    private BigDecimal totalPriceInclVat; // 부가세 포함 총 금액
    private Integer quantitySubtotal; // 수량 소계
    private BigDecimal unitPriceSubtotal; // 단가 소계
    private BigDecimal priceSubtotalVat; // 공급가액(VAT 포함) 소계
    private BigDecimal discountPrice; // 할인 금액
    private BigDecimal unitTruncation; // 단위 절사
    private List<QuotationProductResponse> productList;
}
