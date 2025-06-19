package livart.shop.security.dto.request;

import lombok.Getter;

@Getter
public class PasswordFindRequest {
    private String loginId;
    private String phoneNum;
}
