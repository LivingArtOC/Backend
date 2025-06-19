package livart.common.Auth.dto.request;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String loginId; // 로그인 아이디 or 사업자 등록 번호
    private String password;
}
