package livart.shop.domain.design.brand.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandViewResponse {
    private String fileName;
    private String imageUrl;
    private Long updatedAt; // epoch milli (ETag 생성용)
}