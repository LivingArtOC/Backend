package livart.erp.domain.defaultSetting.policy.dto.request;

import lombok.Getter;

@Getter
public class CourseRequest {
    private Boolean isRequired;
    private String content;
    private String officerName; //보호 책임자 이름
    private String officerPosition; //보호 책임자 직책
    private String officerPhone; //보호 책임자 전번
    private String officerEmail; //보호 책임자 이메일
}
