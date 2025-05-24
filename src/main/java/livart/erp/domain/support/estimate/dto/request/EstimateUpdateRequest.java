package livart.erp.domain.support.estimate.dto.request;

import livart.common.dto.enums.estimate.EstimateStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class EstimateUpdateRequest {
    private String companyName;
    private String managerName;
    private String phoneNum;
    private LocalDate visitDate;
    private LocalDate deliveryDate;
    private String email;
    private List<EstimateProductRequest> productRequests;
    private EstimateStatus status;
    private String memo;
}
