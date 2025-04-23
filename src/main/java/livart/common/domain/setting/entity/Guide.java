package livart.common.domain.setting.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import lombok.*;

@Table(name = "service_guides")
@Getter @Builder(toBuilder = true) @Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Guide extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;
    private String imageUrl;
    private Long updateBy;

    public void updateContent(String content) {
        this.content = content;
    }

    public void update(String imageUrl, String content){
        this.imageUrl = imageUrl;
        this.content = content;
    }
}
