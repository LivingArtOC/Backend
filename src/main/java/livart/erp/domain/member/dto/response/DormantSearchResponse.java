package livart.erp.domain.member.dto.response;

import livart.common.dto.enums.user.Provider;
import livart.common.dto.enums.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class DormantSearchResponse {
    private Long userId;
    private String loginId;
    private String name;
    private Role role;
    private Provider provider;
    private Integer mileage;
    private String phoneNum;
    private LocalDate signupDate;
    private LocalDate transitionDate;

    @Builder
    public DormantSearchResponse(Long userId, String loginId, String name, Role role, Provider provider, Integer mileage, String phoneNum, LocalDateTime signupDate, LocalDateTime transitionDate) {
        this.userId = userId;
        this.loginId = loginId;
        this.name = name;
        this.role = role;
        this.provider = provider;
        this.mileage = mileage;
        this.phoneNum = phoneNum;
        this.signupDate = signupDate != null ? signupDate.toLocalDate() : null;
        this.transitionDate = transitionDate != null ? transitionDate.toLocalDate() : null;
    }
}
