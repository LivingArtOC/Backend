package livart.shop.security.dto.request;


import lombok.Getter;

import java.util.List;

@Getter
public class SocialSignupRequest {
    private String loginId;
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
