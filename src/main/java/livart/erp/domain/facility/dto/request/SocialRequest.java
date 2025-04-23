package livart.erp.domain.facility.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import livart.common.dto.enums.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class SocialRequest {
    @NotNull
    private Provider provider;
    @NotBlank
    private String clientId;
    @NotBlank
    private String clientSecret;
}
