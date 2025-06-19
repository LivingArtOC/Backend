package livart.erp.domain.defaultSetting.admin.dto.response;

import livart.common.dto.enums.user.Provider;
import livart.common.dto.enums.user.Role;
import livart.common.dto.enums.user.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true) @Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdminResponse {
    private Long adminId;
    private String loginId;
    private Role role;
    private Provider provider;
    private UserStatus status;
    private Boolean adminRegister;
    private String adminName;
    private String department;
    private String position;
    private String roleTitle;
    private Boolean smsNotiEnabled;
    private String email;
    private String phoneNum;
    private String officeNum;
    private Boolean loginEnabled;
    private String adminMemo;
    private Boolean limitIpAccess;
    private List<String> ipList;
}
