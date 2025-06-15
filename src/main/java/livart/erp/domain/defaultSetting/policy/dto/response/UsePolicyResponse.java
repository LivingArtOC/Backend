package livart.erp.domain.defaultSetting.policy.dto.response;

import livart.common.dto.enums.term.TermSuperType;
import livart.common.dto.enums.term.TermType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder @Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsePolicyResponse {
    private Long termId;
    private Boolean isRequired;
    private String title;
    private TermSuperType superType;
    private TermType type;
    private String usePolicyContent;
    private String courseContent;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isExposed;
    private String officerName; //보호 책임자 이름
    private String officerPosition; //보호 책임자 직책
    private String officerPhone; //보호 책임자 전번
    private String officerEmail; //보호 책임자 이메일
}
