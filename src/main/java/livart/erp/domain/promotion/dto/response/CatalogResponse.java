package livart.erp.domain.promotion.dto.response;

import livart.common.dto.enums.CatalogType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogResponse {
    private Long catalogId;
    private CatalogType catalogType;
    private String fileUrl;
}
