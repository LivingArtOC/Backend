package livart.erp.domain.portfolio.dto.response;

import livart.common.dto.enums.portfolio.PortfolioStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class PfStatusResponse {
    private Long portfolioId;
    private String companyName;
    private String location;
    private String concept;
    private String description;
    private PortfolioStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
}
