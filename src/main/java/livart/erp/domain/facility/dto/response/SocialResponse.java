package livart.erp.domain.facility.dto.response;

import livart.common.dto.enums.Provider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialResponse {
    private Provider provider;
    private String clientId;
    private String clientSecret;
    private Long adminId; // 마지막으로 update한 admin의 Id
    private LocalDateTime updatedAt;
}
