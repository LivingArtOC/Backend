package livart.common.domain.support.estimate.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.estimate.FileType;
import lombok.*;

@Table(name = "estimate_file")
@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EstimateFile extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private FileType type;
    private String fileName;
    private String fileUrl;
    private Long updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estimate_id",nullable = false)
    private Estimate estimate;
}
