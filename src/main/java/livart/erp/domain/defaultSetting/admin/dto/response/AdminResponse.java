package livart.erp.domain.defaultSetting.admin.dto.response;

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
