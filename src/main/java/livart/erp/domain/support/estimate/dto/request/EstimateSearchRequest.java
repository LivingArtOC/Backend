package livart.erp.domain.support.estimate.dto.request;

import livart.common.dto.enums.estimate.EstimateStatus;
import livart.common.dto.enums.estimate.EstimateType;
import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

@Getter
public class EstimateSearchRequest {
    private EstimateSearchKey key;
    private String keyword;
    private EstimateStatus status;
    private DateSearchDto applyDate;

}
