package livart.common.domain.support.quotation.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Table(name = "quotation_item")
@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuotationItem extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private String hashCode;
    private String productName;
    private String optionCode;
    private String productSize;
    private String thumbNailImgUrl;
    private Integer quantity;

    @Column(precision = 10, scale = 2)
    private BigDecimal unitOriginalPrice; // 정가(초기 : 제품 정가 + 옵션 가격 이고 이후 수정)

    @Column(precision = 10, scale = 2)
    private BigDecimal unitSalePrice; // 판매 단가 (초기 : 제품 판매가 + 옵션 가격 이고 이후 수정)

    @Column(precision = 10, scale = 2)
    private BigDecimal salePriceVat; // 공급가(수량 X 단가)

    private String note; // 비고

    private Long createdBy;
    private Long updateBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id")
    private Quotation quotation;

    @Builder.Default
    @OneToMany(mappedBy = "quotationItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuotationItemOption> quotationItemOptions = new ArrayList<>();
}
