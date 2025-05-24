package livart.erp.domain.defaultSetting.policy.dto.request;

import jakarta.persistence.Lob;
import livart.common.dto.enums.term.TermType;
import lombok.Getter;

@Getter
public class TermsRequest {
    private String type;
    private String title;
    private Boolean isRequired;
    private String content;
}
