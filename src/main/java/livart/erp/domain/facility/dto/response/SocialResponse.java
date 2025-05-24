package livart.erp.domain.facility.dto.response;

import livart.common.dto.enums.user.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder @Getter
@AllArgsConstructor
@NoArgsConstructor
public class SocialResponse {
    private Provider provider;
    private String clientId;
    private String clientSecret;
}
