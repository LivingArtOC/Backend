package livart.common.domain.portfolio.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.portfolio.ImageType;
import lombok.*;

@Table(name = "portfolio_image") // 납품 사례 목록 이미지
@Entity @Builder @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PortfolioImage extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ImageType imageType;
    private String fileName;
    private String imageUrl;
    private Integer orderIndex;

    @Lob
    private String detailComment;
    private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;
}
