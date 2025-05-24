package livart.erp.domain.defaultSetting.admin.dto.response;

import livart.common.dto.enums.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnableLoginResponse {
    private Long adminId;
    private String loginId;
    private Role role;
    private String adminName;
    private boolean loginEnabled;
}
