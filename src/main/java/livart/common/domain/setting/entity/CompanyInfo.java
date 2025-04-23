package livart.common.domain.setting.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
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

    @Lob
    private String operatingHours;

    @Setter
    private Long updatedBy;
}
