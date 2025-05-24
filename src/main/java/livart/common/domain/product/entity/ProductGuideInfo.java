package livart.common.domain.product.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.product.ProductGuide;
import lombok.*;

@Table(name = "product_guide_info")
@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductGuideInfo extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ProductGuide guide;
    private Integer orderIndex;
    private String imageUrl;
    private String fileName;
    private String text;
    private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public void update(ProductGuide guide, String imageUrl, String fileName, String text, Long updatedBy){
        this.guide = guide;
        this.imageUrl = imageUrl;
        this.fileName = fileName;
        this.text = text;
        this.updatedBy = updatedBy;
    }
}
