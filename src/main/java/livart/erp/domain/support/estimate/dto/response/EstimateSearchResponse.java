package livart.erp.domain.support.estimate.dto.response;

import livart.common.dto.enums.estimate.EstimateStatus;
import livart.common.dto.enums.estimate.EstimateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstimateSearchResponse {
    private Long estimateId;
    private String proposer; // 신청자 아이디 (작성자)
    private String companyName;
    private String managerName;
    private String phoneNum;
    private String email;
    private EstimateStatus status;
}
