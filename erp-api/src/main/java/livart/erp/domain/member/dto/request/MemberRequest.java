package livart.erp.domain.member.dto.request;

import jakarta.persistence.Column;
import livart.common.dto.enums.user.Role;
import livart.common.dto.enums.user.UserStatus;

import lombok.Getter;
import java.util.List;

@Getter
public class MemberRequest {
    private Role role;
    private String loginId;
    private String password;
    private String name;
    private String email;
    private String phoneNum;
    private Boolean emailNotice;
    private Boolean smsNotice;
    private Boolean kakaoNotice;
    private Boolean tmNotice;
    private String zipcode;
    private String address;
    private String detailedAddress;

    private String bizName;
    private String presidentName;
    private String bizRegisterationNum;
    private String bizStatus;
    private String bizType;
    private String faxNum;
    private String managerName;
    private String managerPhoneNum;

    private List<TermsAgreementRequest> agreements;

    @Getter
    public static class TermsAgreementRequest {
        private Long termsId;
        private Boolean isAgreed;
    }
}
