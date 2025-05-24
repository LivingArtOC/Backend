package livart.erp.domain.defaultSetting.policy.dto.response;

import livart.common.dto.enums.term.TermType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder @Getter
@NoArgsConstructor
@AllArgsConstructor
public class TermsResponse {
    private Long termId;
    private TermType type;
    private Boolean required;
    private String title;
    private String content;
}
