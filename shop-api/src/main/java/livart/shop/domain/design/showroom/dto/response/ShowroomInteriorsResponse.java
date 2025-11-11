package livart.shop.domain.design.showroom.dto.response;

import livart.common.domain.design.entity.InteriorsImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowroomInteriorsResponse {
    private Long id;
    private String fileName;
    private String imageUrl;
    private Integer orderIndex;

    public static ShowroomInteriorsResponse from(InteriorsImage e) {
        return ShowroomInteriorsResponse.builder()
                .id(e.getId())
                .fileName(e.getFileName())
                .imageUrl(e.getImageUrl())
                .orderIndex(e.getOrderIndex())
                .build();
    }
}