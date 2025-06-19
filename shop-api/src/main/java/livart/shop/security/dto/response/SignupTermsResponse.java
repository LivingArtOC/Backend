package livart.shop.security.dto.response;

import livart.common.dto.enums.term.TermType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupTermsResponse {
    private Long termId;
    private Boolean isRequired;
    private String title;
    private TermType type;
    private String content;
}
