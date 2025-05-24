package livart.common.domain.design.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import lombok.*;

@Table(name = "interior_info")
@Entity @Builder @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InteriorsInfo extends BaseTime {
    @Id
    @Builder.Default
    private Long id = 1L;
    private String email;
    private String faxNum;

    @Lob
    private String directions;
    @Lob
    private String usageGuide;

    private Long createdBy;
    private Long updatedBy;

    public  void update(String email, String faxNum, String directions, String usageGuide, Long updatedBy){
        this.email = email;
        this.faxNum = faxNum;
        this.directions = directions;
        this.usageGuide = usageGuide;
        this.updatedBy = updatedBy;
    }
}
