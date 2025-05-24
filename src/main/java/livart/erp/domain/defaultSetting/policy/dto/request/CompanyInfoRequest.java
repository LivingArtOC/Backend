package livart.erp.domain.defaultSetting.policy.dto.request;


import livart.common.dto.enums.defaultSetting.DayType;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class CompanyInfoRequest {
    private String companyName;
    private String bizNum;
    private String bizName;
    private String presidentName;
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
    private Map<DayType, TimeRange> hours;

    @Getter
    public static class TimeRange {
        private String start;
        private String end;
    }
}
