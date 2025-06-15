package livart.erp.domain.alarm.dto.response;

import livart.common.dto.enums.alarm.AlarmType;
import livart.common.dto.enums.user.Provider;
import livart.common.dto.enums.user.Role;
import livart.common.dto.enums.user.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MemberSearchResponse {
    private Long userId;
    private UserStatus status;
    private String loginId;
    private String userName;
    private Role role;
    private String email;
    private String phoneNum;
    private Boolean emailNotice;
    private Boolean smsNotice;
    @Builder
    public MemberSearchResponse(Long userId, UserStatus status, String loginId, String userName,
                                Role role, String email, String phoneNum, Boolean emailNotice, Boolean smsNotice) {
        this.userId = userId;
        this.status = status;
        this.loginId = loginId;
        this.userName = userName;
        this.role = role;
        this.email = email;
        this.phoneNum = phoneNum;
        this.emailNotice = emailNotice;
        this.smsNotice = smsNotice;
    }

}
