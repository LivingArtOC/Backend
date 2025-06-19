package livart.common.dto.request.user;

import lombok.Getter;

import java.util.List;

@Getter
public class AdminRequest {
    private String loginId;
    private String password;
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
