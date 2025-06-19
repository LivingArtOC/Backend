package livart.common.domain.design.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import lombok.*;

@Table(name = "brand_info")
@Entity @Builder @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Brand extends BaseTime {

    @Id
    @Builder.Default
    private Long id = 1L;
    private String fileName;
    private String imageUrl;
    private Long updatedBy;

    public void update(String fileName, String imageUrl){
        this.fileName = fileName;
        this.imageUrl = imageUrl;
    }
}
