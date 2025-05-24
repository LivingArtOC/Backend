package livart.erp.domain.portfolio.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class PfDisplayUpdateResponse {
    private Long portfolioId;
    private String companyName;
    private Integer orderIndex;
    private Boolean isPinned;
    private LocalDate createdAt;
}
