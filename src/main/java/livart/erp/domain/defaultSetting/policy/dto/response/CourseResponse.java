package livart.erp.domain.defaultSetting.policy.dto.response;

import livart.common.dto.enums.term.TermType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder @Getter
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long termId;
    private Boolean isRequired;
    private String title;
    private TermType type;
    private String content;
    private String officerName; //보호 책임자 이름
    private String officerPosition; //보호 책임자 직책
    private String officerPhone; //보호 책임자 전번
    private String officerEmail; //보호 책임자 이메일
}
