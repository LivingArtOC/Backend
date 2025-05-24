package livart.erp.domain.portfolio.dto.request;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
public class DisplayUpdateRequest {
    private Long portfolioId;
    private Integer orderIndex;
}
