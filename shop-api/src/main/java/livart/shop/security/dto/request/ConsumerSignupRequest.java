package livart.shop.security.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class ConsumerSignupRequest {
    private String loginId;
    private String password;
    private String userName;
    private String phoneNum;
    private String email;
    private String zipcode;
    private String address;
    private String detailedAddress;
    private Boolean defaultAddress;

    private List<TermsAgreementRequest> agreements;

    @Getter
    public static class TermsAgreementRequest {
        private Long termsId;
        private Boolean isAgreed;
    }
}
