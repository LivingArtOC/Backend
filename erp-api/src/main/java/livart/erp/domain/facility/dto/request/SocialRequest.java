package livart.erp.domain.facility.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import livart.common.dto.enums.user.Provider;
import lombok.Getter;

@Getter
public class SocialRequest {
    @NotNull
    private String provider;
    @NotBlank
    private String clientId;
    @NotBlank
    private String clientSecret;
}
