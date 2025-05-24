package livart.erp.domain.member.dto.response;

import livart.common.dto.enums.user.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusResponse {
    private Long userId;
    private String loginId;
    private UserStatus status;
    private LocalDateTime updatedAt;
}
