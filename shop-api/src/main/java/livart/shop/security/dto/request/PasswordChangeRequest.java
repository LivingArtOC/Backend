package livart.shop.security.dto.request;

import lombok.Getter;

@Getter
public class PasswordChangeRequest {
    private String loginId;
    private String phoneNum;
    private String password;
}
