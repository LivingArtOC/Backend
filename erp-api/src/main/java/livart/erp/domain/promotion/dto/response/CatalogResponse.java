package livart.erp.domain.promotion.dto.response;

import livart.common.dto.enums.design.CatalogType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogResponse {
    private Long catalogId;
    private CatalogType catalogType;
    private String fileName;
    private String fileUrl;
}
