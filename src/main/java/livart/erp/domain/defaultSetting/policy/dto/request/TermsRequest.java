package livart.erp.domain.defaultSetting.policy.dto.request;

import jakarta.persistence.Lob;
import lombok.Getter;

@Getter
public class TermsRequest {
    private Long termId;
    private String content;
}
