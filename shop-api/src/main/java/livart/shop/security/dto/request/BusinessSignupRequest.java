package livart.shop.security.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class BusinessSignupRequest {

    private String loginId;
    private String password;
    private String ownerName;
    private String bizName;
    private String bizRegistrationNum;
    private String bizPhoneNum;
    private String email;
    private String zipcode;
    private String address;
    private String detailedAddress;
    private Boolean defaultAddress;
    private String bizStatus;
    private String bizType;
    private String managerName;
    private String managerPhoneNum;
    private String faxNum;

    private List<TermsAgreementRequest> agreements;

    @Getter
    public static class TermsAgreementRequest {
        private Long termsId;
        private Boolean isAgreed;
    }

}
