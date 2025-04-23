package livart.erp.domain.defaultSetting.policy.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true) @Getter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyInfoResponse {
    private String companyName;
    private String bizNum;
    private String bizName;
    private String bizStatus;
    private String bizType;
    private String email;
    private String zipcode;
    private String address; //도로명 주소
    private String detailAddress;
    private String phoneNum; //대표 전화
    private String faxNum;
    private String ecommerceLicense;
    private String companySealURL;
    private String operatingHours;
    private Long updatedBy;
}
