package livart.common.domain.product.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import lombok.*;

@Table(name = "option_mapping")
@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OptionMapping extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "detailed_option_id", nullable = false)
    private DetailedOption detailedOption;

    private Long updatedBy;

}
