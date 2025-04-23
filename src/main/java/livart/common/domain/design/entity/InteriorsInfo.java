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
    private Long id = 1L;
    private String email;
    private String paxNum;

    @Lob
    private String directions;
    @Lob
    private String usageGuide;
    @Lob
    private String operatingHours;
    private Long createdBy;
    private Long updatedBy;

    public  void update(String email, String paxNum, String directions, String usageGuide, String operatingHours,Long updatedBy){
        this.email = email;
        this.paxNum = paxNum;
        this.directions = directions;
        this.usageGuide = usageGuide;
        this.operatingHours = operatingHours;
        this.updatedBy = updatedBy;
    }
}
