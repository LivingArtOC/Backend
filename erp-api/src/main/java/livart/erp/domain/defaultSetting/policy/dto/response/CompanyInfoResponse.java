package livart.erp.domain.defaultSetting.policy.dto.response;


import livart.common.dto.enums.defaultSetting.DayType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.EnumMap;
import java.util.Map;

@Builder(toBuilder = true) @Getter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyInfoResponse {
    private Long companyInfoId;
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
    private Map<DayType, CompanyInfoResponse.TimeRange> hours = new EnumMap<>(DayType.class);

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TimeRange {
        private String start;
        private String end;
    }
}
