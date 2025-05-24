package livart.erp.domain.member.dto.request;

import livart.common.dto.enums.user.Provider;
import livart.common.dto.enums.user.Role;
import livart.common.dto.enums.user.UserStatus;
import livart.common.dto.request.DateSearchDto;
import lombok.Getter;
import org.springframework.security.authorization.method.HandleAuthorizationDenied;

@Getter
public class MemberSearchRequest {
    private SearchKey key;
    private String keyword;
    private Role role;
    private UserStatus status;
    private DateSearchDto signUpDate;
    private DateSearchDto lastLoginDate;
    private IntegerRangeSearch mileage;
    private IntegerRangeSearch orderCount;
    private Boolean emailNotice;
    private Boolean smsNotice;
    private Boolean kakaoNotice;
    private Provider provider;
}
