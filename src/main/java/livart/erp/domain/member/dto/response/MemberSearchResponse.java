package livart.erp.domain.member.dto.response;

import livart.common.dto.enums.user.Provider;
import livart.common.dto.enums.user.Role;
import livart.common.dto.enums.user.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberSearchResponse {
    private Long userId;
    private String loginId;
    private String userName;
    private Role role;
    private Provider provider;
    private Integer mileage;
    private Integer orderCount;
    private String phoneNum;
    private LocalDate signUpDate;
    private LocalDate lastLoginDate;
    private Boolean isDormant;
}
