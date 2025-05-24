package livart.erp.domain.support.quotation.dto.request;

import jakarta.persistence.Column;
import livart.common.dto.enums.quotation.QuotationStatus;
import livart.common.dto.enums.quotation.QuotationType;
import livart.erp.domain.support.quotation.dto.response.PicListResponse;
import livart.erp.domain.support.quotation.dto.response.QuotationProductResponse;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
public class QuotationRequest {
    private Long estimateId;
    private QuotationType type;
    private String phoneNum;
    private String title;
    private String bizNum; // 공급자 정보
    private String corporationName;
    private String presidentName;
    private String address;
    private String detailAddress;
    private String picName;
    private String picPhoneNum;
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
    private List<QuotationProductRequest> productList;
}

