package livart.erp.domain.design.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageListDto {
    private String fileName;
    private String imageUrl;
    private Integer orderIndex;
}
