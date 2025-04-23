package livart.shop.security.service;

import jakarta.servlet.http.HttpServletRequest;
import livart.common.Auth.CustomUserDetails;
import livart.common.domain.address.entity.UserAddress;
import livart.common.domain.address.repository.UserAddressRepository;
import livart.common.domain.notice.entity.UserMKConsent;
import livart.common.domain.notice.repository.UserMKConsentRepository;
import livart.common.domain.term.entity.Terms;
import livart.common.domain.term.entity.UserTerms;
import livart.common.domain.term.repository.TermsRepository;
import livart.common.domain.term.repository.UserTermsRepository;
import livart.common.domain.user.entity.Business;
import livart.common.domain.user.entity.Consumer;
import livart.common.domain.user.entity.User;
import livart.common.dto.enums.Provider;
import livart.common.dto.enums.Role;
import livart.common.dto.enums.TermType;
import livart.common.dto.enums.UserStatus;
import livart.common.domain.user.repository.BusinessRepository;
import livart.common.domain.user.repository.ConsumerRepository;
import livart.common.domain.user.repository.UserRepository;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.log.entity.LoginHistory;
import livart.common.log.repository.LoginHistoryRepository;
import livart.common.service.GlobalService;
import livart.shop.security.dto.request.BusinessSignupRequest;
import livart.shop.security.dto.request.ConsumerSignupRequest;
import livart.shop.security.dto.request.SocialSignupRequest;
import livart.shop.security.dto.response.SignupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ConsumerRepository consumerRepository;
    private final UserAddressRepository userAddressRepository;
    private final UserMKConsentRepository userMKConsentRepository;
    private final BusinessRepository businessRepository;
    private final TermsRepository termsRepository;
    private final UserTermsRepository userTermsRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final GlobalService globalService;
    private final LoginHistoryRepository loginHistoryRepository;

    @Transactional
    public SignupResponse signupConsumer(ConsumerSignupRequest request){
        validateLoginId(request.getLoginId());
        validatePassword(request.getPassword());

        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        Consumer consumer = Consumer
                .builder()
                .loginId(request.getLoginId())
                .password(encodedPassword)
                .email(request.getEmail())
                .role(Role.CONSUMER)
                .provider(Provider.LOCAL)
                .status(UserStatus.ACTIVE)
                .name(request.getUserName())
                .phoneNum(request.getPhoneNum())
                .build();

        Consumer savedConsumer = consumerRepository.save(consumer);

        UserAddress address = UserAddress.builder()
                .zipcode(request.getZipcode())
                .address(request.getAddress())
                .detailedAddress(request.getDetailedAddress())
                .defaultAddress(request.getDefaultAddress())
                .user(savedConsumer)
                .build();

        userAddressRepository.save(address);

        List<UserTerms> userTerms = request.getAgreements()
                .stream()
                .map(agreement -> {
                    Terms terms = termsRepository.findById(agreement.getTermsId())
                            .orElseThrow(() -> new CustomException(ErrorCode.TERM_NOT_FOUND));

                    return UserTerms.builder()
                            .isAgreed(agreement.getIsAgreed())
                            .user(savedConsumer)
                            .terms(terms)
                            .build();
                })
                .collect(Collectors.toList());

        userTermsRepository.saveAll(userTerms);

        Boolean marketingConsent = request.getAgreements().stream()
                .filter(agreement -> {
                    Terms terms = termsRepository.findById(agreement.getTermsId())
                            .orElseThrow(() -> new CustomException(ErrorCode.TERM_NOT_FOUND));
                    return terms.getType() == TermType.MARKETING;
                })
                .map(ConsumerSignupRequest.TermsAgreementRequest::getIsAgreed)
                .findFirst()
                .orElse(null);

        if (marketingConsent.equals(true) || marketingConsent.equals(false)) {
            UserMKConsent consent = UserMKConsent.builder()
                    .smsNotice(marketingConsent)
                    .emailNotice(marketingConsent)
                    .kakaoNotice(marketingConsent)
                    .tmNotice(marketingConsent)
                    .user(savedConsumer)
                    .build();
            userMKConsentRepository.save(consent);
        } else {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }


        return SignupResponse.builder()
                .loginId(savedConsumer.getLoginId())
                .userName(savedConsumer.getName())
                .email(savedConsumer.getEmail())
                .role(savedConsumer.getRole().name())
                .createdAt(savedConsumer.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .build();

    }
    @Transactional
    public SignupResponse signupBiz(BusinessSignupRequest request){

        if(businessRepository.findBusinessByBizRegistrationNum(request.getBizRegistrationNum()).isPresent()){
            throw new CustomException(ErrorCode.DUPLICATE_BIZ_NUM);
        }

        validateLoginId(request.getLoginId());
        validatePassword(request.getPassword());

        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        Business business = Business
                .builder()
                .loginId(request.getLoginId())
                .password(encodedPassword)
                .ownerName(request.getOwnerName())
                .bizName(request.getBizName())
                .bizRegistrationNum(request.getBizRegistrationNum())
                .bizPhoneNum(request.getBizPhoneNum())
                .email(request.getEmail())
                .role(Role.BUSINESS)
                .provider(Provider.LOCAL)
                .status(UserStatus.ACTIVE)
                .bizStatus(request.getBizStatus())
                .bizType(request.getBizType())
                .managerName(request.getManagerName())
                .managerPhoneNum(request.getManagerPhoneNum())
                .faxNum(request.getFaxNum())
                .build();

        Business savedBiz = businessRepository.save(business);

        UserAddress address = UserAddress.builder()
                .zipcode(request.getZipcode())
                .address(request.getAddress())
                .detailedAddress(request.getDetailedAddress())
                .defaultAddress(request.getDefaultAddress())
                .user(savedBiz)
                .build();

        userAddressRepository.save(address);

        List<UserTerms> userTerms = request.getAgreements()
                .stream()
                .map(agreement -> {
                    Terms terms = termsRepository.findById(agreement.getTermsId())
                            .orElseThrow(() -> new CustomException(ErrorCode.TERM_NOT_FOUND));

                    return UserTerms.builder()
                            .isAgreed(agreement.getIsAgreed())
                            .user(savedBiz)
                            .terms(terms)
                            .build();
                })
                .collect(Collectors.toList());

        userTermsRepository.saveAll(userTerms);

        Boolean marketingConsent = request.getAgreements().stream()
                .filter(agreement -> {
                    Terms terms = termsRepository.findById(agreement.getTermsId())
                            .orElseThrow(() -> new CustomException(ErrorCode.TERM_NOT_FOUND));
                    return terms.getType() == TermType.MARKETING;
                })
                .map(ConsumerSignupRequest.TermsAgreementRequest::getIsAgreed)
                .findFirst()
                .orElse(null);

        if (marketingConsent.equals(true) || marketingConsent.equals(false)) {
            UserMKConsent consent = UserMKConsent.builder()
                    .smsNotice(marketingConsent)
                    .emailNotice(marketingConsent)
                    .kakaoNotice(marketingConsent)
                    .tmNotice(marketingConsent)
                    .user(savedBiz)
                    .build();
            userMKConsentRepository.save(consent);
        } else {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        return SignupResponse.builder()
                .loginId(savedBiz.getLoginId())
                .userName(savedBiz.getBizName())
                .email(savedBiz.getEmail())
                .role(savedBiz.getRole().name())
                .createdAt(savedBiz.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .build();

    }

    public SignupResponse signupSocial(SocialSignupRequest request, CustomUserDetails customUserDetails, HttpServletRequest servletRequest){
        validateLoginId(request.getLoginId());

        User user = globalService.findUser(customUserDetails);

        Consumer consumer = Consumer
                .fromUser(user, request.getLoginId(),request.getPhoneNum(), request.getEmail());

        Consumer savedConsumer = consumerRepository.save(consumer);

        loginHistoryRepository.save(LoginHistory.builder()
                .loginId(savedConsumer.getLoginId())
                .userId(savedConsumer.getId())
                .ipAddress(servletRequest.getRemoteAddr())
                .userAgent(servletRequest.getHeader("User-Agent"))
                .success(true)
                .site("SHOP")
                .attemptedAt(Instant.now())
                .build());


        UserAddress address = UserAddress.builder()
                .zipcode(request.getZipcode())
                .address(request.getAddress())
                .detailedAddress(request.getDetailedAddress())
                .defaultAddress(request.getDefaultAddress())
                .user(savedConsumer)
                .build();

        userAddressRepository.save(address);

        List<UserTerms> userTerms = request.getAgreements()
                .stream()
                .map(agreement -> {
                    Terms terms = termsRepository.findById(agreement.getTermsId())
                            .orElseThrow(() -> new CustomException(ErrorCode.TERM_NOT_FOUND));

                    return UserTerms.builder()
                            .isAgreed(agreement.getIsAgreed())
                            .user(savedConsumer)
                            .terms(terms)
                            .build();
                })
                .collect(Collectors.toList());

        userTermsRepository.saveAll(userTerms);

        Boolean marketingConsent = request.getAgreements().stream()
                .filter(agreement -> {
                    Terms terms = termsRepository.findById(agreement.getTermsId())
                            .orElseThrow(() -> new CustomException(ErrorCode.TERM_NOT_FOUND));
                    return terms.getType() == TermType.MARKETING;
                })
                .map(SocialSignupRequest.TermsAgreementRequest::getIsAgreed)
                .findFirst()
                .orElse(null);

        if (marketingConsent.equals(true) || marketingConsent.equals(false) ) {
            UserMKConsent consent = UserMKConsent.builder()
                    .smsNotice(marketingConsent)
                    .emailNotice(marketingConsent)
                    .kakaoNotice(marketingConsent)
                    .tmNotice(marketingConsent)
                    .user(savedConsumer)
                    .build();
            userMKConsentRepository.save(consent);
        } else {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        return SignupResponse.builder()
                .loginId(savedConsumer.getLoginId())
                .userName(savedConsumer.getName())
                .email(savedConsumer.getEmail())
                .role(savedConsumer.getRole().name())
                .createdAt(savedConsumer.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .build();
    }

    public void validatePassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 16) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD_FORMAT);
        }

        int count = 0;

        if (password.matches(".*[A-Z].*")) count++;               // 대문자
        if (password.matches(".*[a-z].*")) count++;               // 소문자
        if (password.matches(".*\\d.*")) count++;                 // 숫자
        if (password.matches(".*[!@#$%^&*()].*")) count++;        // 특수문자

        if (count < 3) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD_FORMAT);
        }
    }

    // 아이디 중복 확인 + 형식 검증
    public void validateLoginId(String loginId) {
        // 1. 정규식: 영소문자 + 숫자 조합, 6~12자
        String regex = "^[a-z0-9]{6,12}$";
        if (!Pattern.matches(regex, loginId)) {
            throw new CustomException(ErrorCode.INVALID_LOGIN_ID_FORMAT);
        }
        // 2. DB 중복 확인
        if (userRepository.existsByLoginId(loginId)) {
            throw new CustomException(ErrorCode.DUPLICATED_LOGIN_ID);
        }
    }

}
