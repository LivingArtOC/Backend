package livart.common.domain.product.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.product.ImageType;
import lombok.*;

@Table(name = "product_image")
@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ImageType imageType;
    private Integer orderIndex;
    private String imageUrl;
    private String fileName;
    private Long updatedBy;

    @Lob
    private String detailText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public void update(ImageType imageType, String imageUrl, String fileName,String detailText, Long updatedBy){
        this.imageType = imageType;
        this.imageUrl = imageUrl;
        this.fileName = fileName;
        this.detailText = detailText;
        this.updatedBy = updatedBy;
    }
}
