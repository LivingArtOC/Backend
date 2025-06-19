package livart.common.domain.setting.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.defaultSetting.GuideType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "guide")
@Getter @Builder @Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Guide extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private GuideType type;

    @Lob
    private String content;
    private Long updateBy;

    @Builder.Default
    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GuideImage> guideImages = new ArrayList<>();

    public void updateContent(String content, Long updateBy) {
        this.content = content;
        this.updateBy = updateBy;
    }

}
