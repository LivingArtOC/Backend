package livart.erp.domain.member.dto.response;

import livart.common.dto.enums.user.Role;
import livart.common.dto.enums.user.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteSearchResponse {
    private Long userId;
    private String loginId;

    private Role role;
    private UserStatus deleteType;
    private LocalDate deleteAt;
    private Boolean recoverable;
    private String withdrawIp;
    private String agent;
    private String reason;
}
