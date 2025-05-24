package livart.erp.domain.support.estimate.dto.response;

import livart.common.dto.enums.estimate.EstimateStatus;
import livart.common.dto.enums.estimate.EstimateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstimateResponse {
    private Long estimateId;
    private String proposer; // 신청자(작성자)
    private String companyName;
    private String managerName;
    private String phoneNum;
    private String email;
    private LocalDate preferVisitDate;
    private LocalDate preferDeliveryDate;
    private Boolean emailAlarm;
    private Boolean kakaoAlarm;
    private List<FileInfoResponse> referenceImgList;
    private List<FileInfoResponse> spaceImgList;
    private List<FileInfoResponse> attachFileList;
    private List<EstimateProductResponse> productList;
    private String content;
    private Boolean isAgreed;

    private EstimateStatus status;
    private String memo;
}
