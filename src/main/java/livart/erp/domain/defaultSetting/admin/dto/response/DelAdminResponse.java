package livart.erp.domain.defaultSetting.admin.dto.response;

import livart.common.dto.enums.Role;
import livart.common.dto.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class DelAdminResponse {
    private Long adminId;
    private String loginId;
    private Role role;
    private UserStatus status;
}
