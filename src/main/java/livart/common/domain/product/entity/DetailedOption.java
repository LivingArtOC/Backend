package livart.common.domain.product.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "detailed_option")
@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DetailedOption extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer orderIndex;
    private String optionName; // 옵션 명
    private String valueName; // 옵션 값
    private String imageUrl; // 옵션 이미지
    private String fileName; // 이미지 파일 명
    private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Builder.Default
    @OneToMany(mappedBy = "detailedOption", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionMapping> optionMappings = new ArrayList<>();

    public void updateDetailOption(String valueName, String imageUrl, String fileName, Long updatedBy) {
        this.valueName = valueName;
        this.imageUrl = imageUrl;
        this.fileName = fileName;
        this.updatedBy = updatedBy;
    }
}
