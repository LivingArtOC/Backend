package livart.erp.domain.defaultSetting.policy.dto.response;

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
    private TermType type;
    private String content;
    private String course;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isExposed;
}
