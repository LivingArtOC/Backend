package livart.shop.security.dto.response;

import livart.common.dto.enums.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupResponse {
    private String loginId;
    private String userName;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
}
