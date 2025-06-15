package livart.erp.domain.member.dto.response;

import livart.common.dto.enums.user.Role;
import livart.erp.domain.member.dto.request.MemberRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {
    private Long memberId;
    private Role role;
    private String loginId;
    private String name;
    private String email;
    private String phoneNum;
    private Boolean emailNotice;
    private Boolean smsNotice;
    private Boolean kakaoNotice;
    private String zipcode;
    private String address;
    private String detailedAddress;
    private BizInfoResponse bizInfo;
    private String memo;
    private List<MemberResponse.TermsAgreementResponse> agreements;

    @Getter @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TermsAgreementResponse {
        private Long termsId;
        private String termTitle;
        private Boolean isAgreed;
    }
}
