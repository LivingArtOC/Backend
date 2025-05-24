package livart.common.domain.setting.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.erp.domain.defaultSetting.policy.dto.request.CompanyInfoRequest;
import lombok.*;

@Table(name = "company_info")
@Entity @Builder(toBuilder = true) @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompanyInfo extends BaseTime {
    @Id
    @Builder.Default
    private Long id = 1L;

    private String companyName;
    private String bizNum;
    private String bizName;
    private String presidentName; // 대표자 명
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
    private Long updatedBy;

    public void updateEntityFromRequest(CompanyInfoRequest request, Long updatedBy) {
        this.companyName = request.getCompanyName();
        this.bizNum = request.getBizNum();
        this.bizName = request.getBizName();
        this.presidentName = request.getPresidentName();
        this.bizStatus = request.getBizStatus();
        this.bizType = request.getBizType();
        this.email = request.getEmail();
        this.zipcode = request.getZipcode();
        this.address = request.getAddress();
        this.detailAddress = request.getDetailAddress();
        this.phoneNum = request.getPhoneNum();
        this.faxNum = request.getFaxNum();
        this.ecommerceLicense = request.getEcommerceLicense();
        this.companySealURL = request.getCompanySealURL();
        this.updatedBy = updatedBy;
    }
}
