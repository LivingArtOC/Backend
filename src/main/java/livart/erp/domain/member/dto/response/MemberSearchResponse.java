package livart.erp.domain.member.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import livart.common.dto.enums.user.Provider;
import livart.common.dto.enums.user.Role;
import livart.common.dto.enums.user.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Slf4j
@NoArgsConstructor
public class MemberSearchResponse {
    private Long userId;
    private String loginId;
    private String userName;
    private Role role;
    private Provider provider;
    private Integer mileage;
    private Long orderCount;
    private String phoneNum;
    private LocalDate signUpDate;
    private LocalDate lastLoginDate;
    private Boolean isDormant;

    @Builder
    public MemberSearchResponse(Long userId, String loginId, String userName, Role role,
                                Provider provider, Integer mileage, Long orderCount,
                                String phoneNum, LocalDateTime signUpDate, LocalDateTime lastLoginDate, UserStatus status) {
        this.userId = userId;
        this.loginId = loginId;
        this.userName = userName;
        this.role = role;
        this.provider = provider;
        this.mileage = mileage;
        this.orderCount = orderCount;
        this.phoneNum = phoneNum;
        this.signUpDate = signUpDate != null ? signUpDate.toLocalDate() : null;
        this.lastLoginDate = lastLoginDate != null ? lastLoginDate.toLocalDate() : null;
        this.isDormant = status == UserStatus.ADMIN_DORMANT || status == UserStatus.DORMANT ? true : false;
    }
}
