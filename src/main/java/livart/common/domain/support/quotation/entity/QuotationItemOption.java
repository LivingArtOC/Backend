package livart.common.domain.support.quotation.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "quotation_item_option")
@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuotationItemOption extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String optionName;
    private String optionValue;
    private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotationItem_id")
    private QuotationItem quotationItem;

}
