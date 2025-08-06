package livart.erp.domain.design.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductBannerResponse {
    private String fileName;
    private String imageUrl;
    private String linkUrl;
    private Integer orderIndex;
}
