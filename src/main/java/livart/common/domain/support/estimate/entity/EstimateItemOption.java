package livart.common.domain.support.estimate.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import lombok.*;

@Table(name = "estimate_item_option")
@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EstimateItemOption extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String optionName; // 옵션 명
    private String valueName; // 옵션 값
    private String imageUrl; // 옵션 이미지
    private String fileName; // 이미지 파일 명
    private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estimateItem_id")
    private EstimateItem estimateItem;

}
