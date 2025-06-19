package livart.common.domain.product.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.product.ColorType;
import lombok.*;

@Table(name = "product_color")
@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductColor extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ColorType colorType;
    private Integer orderIndex;
    private String colorCode;
    private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public void update(ColorType colorType, String colorCode, Long updatedBy){
        this.colorType = colorType;
        this.colorCode = colorCode;
        this.updatedBy = updatedBy;
    }
}
