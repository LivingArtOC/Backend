package livart.erp.domain.defaultSetting.policy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder()
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private String title;
    private String content;
    private String officerName; //보호 책임자 이름
    private String officerPosition; //보호 책임자 직책
    private String officerPhone; //보호 책임자 전번
    private String officerEmail; //보호 책임자 이메일
}
