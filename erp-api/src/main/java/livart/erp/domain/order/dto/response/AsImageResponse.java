package livart.erp.domain.order.dto.response;

import livart.common.dto.enums.order.AsImageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsImageResponse {
    private Long imageId;
    private AsImageType imageType;
    private String fileName;
    private String fileUrl;
}
