package livart.erp.domain.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class PfDisplayResponse {
    private Long portfolioId;
    private String thumbNailImgUrl;
    private String companyName;
    private Integer orderIndex;
    private Boolean isPinned;
    private LocalDate createdAt;
}
