package livart.erp.domain.portfolio.dto.request;

import livart.common.dto.enums.portfolio.PortfolioStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class PfStatusRequest {
    private List<Long> idList;
    private PortfolioStatus updateStatus;
}
