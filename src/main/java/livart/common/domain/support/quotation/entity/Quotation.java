package livart.common.domain.support.quotation.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.domain.support.estimate.entity.Estimate;
import livart.common.dto.enums.personalPayment.PersonalPaymentStatus;
import livart.common.dto.enums.quotation.QuotationStatus;
import livart.common.dto.enums.quotation.QuotationType;
import livart.erp.domain.support.quotation.dto.request.QuotationRequest;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Table(name = "quotation")
@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quotation extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private QuotationType type;

    private Long estimateId; // 없을수도
    private String phoneNum;
    private String proposer; // 견적 문의한 아이디, null 가능
    private String author; // 견적서 작성자 아이디, null 가능
    private String title; // 견적서 제목
    private String bizNum; // 공급자 정보
    private String corporationName; // 법인명(상호)
    private String presidentName; // 대표명
    private String address; // 주소
    private String detailAddress; // 세부 주소
    private String picName; // 담당자
    private String picPhoneNum; // 연락처

    @Enumerated(EnumType.STRING)
    private QuotationStatus status; // 결제 여부

    private LocalDate date; // 일자
    private String managerCompanyName; // 대상
    private String managerName; // 대상

    @Column(precision = 10, scale = 2)
    private BigDecimal totalPriceExclVat; // 부가세 별도 총 금액

    @Column(precision = 10, scale = 2)
    private BigDecimal totalPriceInclVat; // 부가세 포함 총 금액

    private Integer quantitySubtotal; // 수량 소계

    @Column(precision = 10, scale = 2)
    private BigDecimal unitSubtotalVat; // 단가 소계

    @Column(precision = 10, scale = 2)
    private BigDecimal priceSubtotal; // 공급가액(VAT 포함) 소계

    @Column(precision = 10, scale = 2)
    private BigDecimal discountPrice; // 할인 금액

    @Column(precision = 10, scale = 2)
    private BigDecimal unitTruncation; // 단위 절사

    @Lob
    private String memo;

    private Long createdBy;
    private Long updatedBy;

    @Builder.Default
    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuotationItem> quotationItems = new ArrayList<>();

    public void update(QuotationRequest request, Long updatedBy, String proposer, String title, String author){
        this.estimateId = request.getEstimateId();
        this.phoneNum = request.getPhoneNum();
        this.proposer = proposer;
        this.title = title;
        this.author = author;
        this.bizNum = request.getBizNum();
        this.corporationName = request.getCorporationName();
        this.presidentName =request.getPresidentName();
        this.address = request.getAddress();
        this.detailAddress = request.getDetailAddress();
        this.picName = request.getPicName();
        this.picPhoneNum = request.getPicPhoneNum();
        this.date = request.getDate();
        this.managerName = request.getManagerName();
        this.managerCompanyName = request.getManagerCompanyName();
        this.memo = request.getMemo();
        this.totalPriceExclVat = request.getTotalPriceExclVat();
        this.totalPriceInclVat = request.getTotalPriceInclVat();
        this.quantitySubtotal = request.getQuantitySubtotal();
        this.unitSubtotalVat = request.getUnitPriceSubtotal();
        this.priceSubtotal = request.getPriceSubtotalVat();
        this.discountPrice = request.getDiscountPrice();
        this.unitTruncation = request.getUnitTruncation();
        this.updatedBy = updatedBy;
    }
}
