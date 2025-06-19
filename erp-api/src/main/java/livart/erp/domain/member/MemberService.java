package livart.erp.domain.member;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import livart.common.Auth.CustomUserDetails;
import livart.common.domain.address.entity.UserAddress;
import livart.common.domain.alarm.entity.QUserMKConsent;
import livart.common.domain.alarm.entity.UserMKConsent;
import livart.common.domain.member.entity.CouponLog;
import livart.common.domain.member.entity.UserCoupon;
import livart.common.domain.member.repository.CouponLogRepository;
import livart.common.domain.member.repository.UserCouponRepository;
import livart.common.domain.order.entity.QOrder;
import livart.common.domain.promotion.entity.Coupon;
import livart.common.domain.term.entity.Term;
import livart.common.domain.term.entity.UserTerm;
import livart.common.domain.term.repository.TermRepository;
import livart.common.domain.term.repository.UserTermRepository;
import livart.common.domain.user.entity.*;
import livart.common.domain.user.repository.BusinessRepository;
import livart.common.domain.user.repository.ConsumerRepository;
import livart.common.domain.user.repository.UserRepository;
import livart.common.dto.enums.ActionType;
import livart.common.dto.enums.coupon.CouponExpiration;
import livart.common.dto.enums.coupon.CouponStatus;
import livart.common.dto.enums.user.*;
import livart.common.log.entity.QUserStatusLog;
import livart.common.log.repository.MileageLogRepository;
import livart.common.log.repository.UserStatusLogRepository;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.log.entity.MileageLog;
import livart.common.log.entity.UserStatusLog;
import livart.common.mapper.SearchResult;
import livart.common.service.GlobalService;
import livart.erp.domain.member.dto.request.*;
import livart.erp.domain.member.dto.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {
    private final GlobalService globalService;
    private final JPAQueryFactory jpaQueryFactory;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final ConsumerRepository consumerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TermRepository termRepository;
    private final UserTermRepository userTermRepository;
    private final MileageLogRepository mileageLogRepository;
    private final UserCouponRepository userCouponRepository;
    private final CouponLogRepository couponLogRepository;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Transactional
    public MemberResponse registerMember(CustomUserDetails customUserDetails, MemberRequest request) {
        globalService.validateAdmin(customUserDetails);

        validateLoginId(request.getLoginId());
        validatePassword(request.getPassword());

        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        if (request.getRole() == Role.CONSUMER) {
            Consumer consumer = Consumer.builder()
                    .loginId(request.getLoginId())
                    .password(encodedPassword)
                    .email(request.getEmail())
                    .role(request.getRole())
                    .provider(Provider.LOCAL)
                    .status(UserStatus.ACTIVE)
                    .adminRegister(true)
                    .name(request.getName())
                    .userName(request.getName())
                    .phoneNum(request.getPhoneNum())
                    .build();

            applyCommonRelations(consumer, request);
            Consumer saved = consumerRepository.save(consumer);
            return buildConsumerResponse(saved);

        } else if (request.getRole() == Role.BUSINESS) {
            Business business = Business.builder()
                    .loginId(request.getLoginId())
                    .password(encodedPassword)
                    .email(request.getEmail())
                    .role(request.getRole())
                    .provider(Provider.LOCAL)
                    .status(UserStatus.ACTIVE)
                    .phoneNum(request.getPhoneNum())
                    .adminRegister(true)
                    .bizName(request.getBizName())
                    .ownerName(request.getPresidentName())
                    .bizRegistrationNum(request.getBizRegisterationNum())
                    .bizStatus(request.getBizStatus())
                    .bizType(request.getBizType())
                    .faxNum(request.getFaxNum())
                    .managerName(request.getManagerName())
                    .userName(request.getBizName())
                    .managerPhoneNum(request.getManagerPhoneNum())
                    .build();

            applyCommonRelations(business, request);
            Business saved = businessRepository.save(business);
            return buildBusinessResponse(saved);

        } else {
            throw new CustomException(ErrorCode.INVALID_ROLE);
        }
    }

    private MemberResponse buildConsumerResponse(Consumer consumer) {
        UserMKConsent consent = consumer.getUserMarketingNotices().stream().findFirst().orElse(null);
        UserAddress adr = consumer.getUserAddresses().stream().findFirst().orElse(null);

        List<MemberResponse.TermsAgreementResponse> terms = userTermRepository.findAllByUserId(consumer.getId()).stream()
                .map(t -> MemberResponse.TermsAgreementResponse.builder()
                        .termsId(t.getTerm().getId())
                        .termTitle(t.getTerm().getTitle())
                        .isAgreed(t.getIsAgreed())
                        .build())
                .collect(Collectors.toList());

        return MemberResponse.builder()
                .memberId(consumer.getId())
                .role(consumer.getRole())
                .loginId(consumer.getLoginId())
                .name(consumer.getName())
                .email(consumer.getEmail())
                .phoneNum(consumer.getPhoneNum())
                .emailNotice(consent != null && consent.getEmailNotice())
                .smsNotice(consent != null && consent.getSmsNotice())
                .kakaoNotice(consent != null && consent.getKakaoNotice())
                .zipcode(adr != null ? adr.getZipcode() : null)
                .address(adr != null ? adr.getAddress() : null)
                .detailedAddress(adr != null ? adr.getDetailedAddress() : null)
                .agreements(terms)
                .memo(consumer.getAdminMemo())
                .build();
    }

    private MemberResponse buildBusinessResponse(Business business) {
        UserMKConsent consent = business.getUserMarketingNotices().stream().findFirst().orElse(null);
        UserAddress adr = business.getUserAddresses().stream().findFirst().orElse(null);

        List<MemberResponse.TermsAgreementResponse> terms = userTermRepository.findAllByUserId(business.getId()).stream()
                .map(t -> MemberResponse.TermsAgreementResponse.builder()
                        .termsId(t.getTerm().getId())
                        .termTitle(t.getTerm().getTitle())
                        .isAgreed(t.getIsAgreed())
                        .build())
                .collect(Collectors.toList());

        BizInfoResponse bizInfo = BizInfoResponse.builder()
                .bizName(business.getBizName())
                .presidentName(business.getOwnerName())
                .bizRegisterationNum(business.getBizRegistrationNum())
                .bizStatus(business.getBizStatus())
                .bizType(business.getBizType())
                .faxNum(business.getFaxNum())
                .managerName(business.getManagerName())
                .managerPhoneNum(business.getManagerPhoneNum())
                .build();

        return MemberResponse.builder()
                .memberId(business.getId())
                .role(business.getRole())
                .loginId(business.getLoginId())
                .name(business.getBizName())
                .email(business.getEmail())
                .phoneNum(business.getPhoneNum())
                .emailNotice(consent != null && consent.getEmailNotice())
                .smsNotice(consent != null && consent.getSmsNotice())
                .kakaoNotice(consent != null && consent.getKakaoNotice())
                .zipcode(adr != null ? adr.getZipcode() : null)
                .address(adr != null ? adr.getAddress() : null)
                .detailedAddress(adr != null ? adr.getDetailedAddress() : null)
                .agreements(terms)
                .bizInfo(bizInfo)
                .memo(business.getAdminMemo())
                .build();
    }


    private void applyCommonRelations(User user, MemberRequest request) {
        user.getUserAddresses().add(buildUserAddress(user, request));
        user.getUserTerms().addAll(buildUserTerms(user, request));
        user.getUserMarketingNotices().add(buildMarketingConsent(user, request));
    }

    private UserAddress buildUserAddress(User user, MemberRequest request) {
        return UserAddress.builder()
                .zipcode(request.getZipcode())
                .address(request.getAddress())
                .detailedAddress(request.getDetailedAddress())
                .defaultAddress(true)
                .user(user)
                .build();
    }

    private List<UserTerm> buildUserTerms(User user, MemberRequest request) {
        return request.getAgreements().stream()
                .map(agreement -> {
                    Term term = termRepository.findById(agreement.getTermsId())
                            .orElseThrow(() -> new CustomException(ErrorCode.TERM_NOT_FOUND));
                    return UserTerm.builder()
                            .isAgreed(agreement.getIsAgreed())
                            .user(user)
                            .term(term)
                            .build();
                }).collect(Collectors.toList());
    }

    private UserMKConsent buildMarketingConsent(User user, MemberRequest request) {
        return UserMKConsent.builder()
                .emailNotice(request.getEmailNotice())
                .smsNotice(request.getSmsNotice())
                .kakaoNotice(request.getKakaoNotice())
                .tmNotice(request.getTmNotice())
                .user(user)
                .build();
    }



    public MemberResponse getMemberInfo(CustomUserDetails customUserDetails, Long userId, HttpServletRequest request) {
        globalService.validateAdmin(customUserDetails);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        globalService.log(
                customUserDetails.getId(),
                customUserDetails.getUsername(),
                ActionType.GET,
                request.getRequestURI(),
                "user",
                userId,
                request.getRemoteAddr(),
                "회원 세부 정보 조회"
        );

        if (user.getRole() == Role.CONSUMER) {
            Consumer consumer = consumerRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            UserMKConsent consent = consumer.getUserMarketingNotices().stream()
                    .findFirst()
                    .orElse(null);

            UserAddress adr = consumer.getUserAddresses().stream()
                    .findFirst()
                    .orElse(null);

            List<MemberResponse.TermsAgreementResponse> terms = userTermRepository.findAllByUserId(consumer.getId()).stream()
                    .map(t -> MemberResponse.TermsAgreementResponse.builder()
                            .termsId(t.getTerm().getId())
                            .termTitle(t.getTerm().getTitle())
                            .isAgreed(t.getIsAgreed())
                            .build()
                    ).collect(Collectors.toList());

            return MemberResponse.builder()
                    .memberId(consumer.getId())
                    .role(consumer.getRole())
                    .loginId(consumer.getLoginId())
                    .name(consumer.getName())
                    .email(consumer.getEmail())
                    .phoneNum(consumer.getPhoneNum())
                    .emailNotice(consent.getEmailNotice())
                    .smsNotice(consent.getSmsNotice())
                    .kakaoNotice(consent.getKakaoNotice())
                    .zipcode(adr.getZipcode())
                    .address(adr.getAddress())
                    .detailedAddress(adr.getDetailedAddress())
                    .agreements(terms)
                    .memo(consumer.getAdminMemo())
                    .build();

        } else if (user.getRole() == Role.BUSINESS) {
            
            Business business = businessRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            UserMKConsent consent = business.getUserMarketingNotices().stream()
                    .findFirst()
                    .orElse(null);

            UserAddress adr = business.getUserAddresses().stream()
                    .findFirst()
                    .orElse(null);

            List<MemberResponse.TermsAgreementResponse> terms = userTermRepository.findAllByUserId(business.getId()).stream()
                    .map(t -> MemberResponse.TermsAgreementResponse.builder()
                            .termsId(t.getTerm().getId())
                            .termTitle(t.getTerm().getTitle())
                            .isAgreed(t.getIsAgreed())
                            .build()
                    ).collect(Collectors.toList());

            BizInfoResponse bizInfoResponse = BizInfoResponse.builder()
                    .bizName(business.getBizName())
                    .presidentName(business.getOwnerName())
                    .bizRegisterationNum(business.getBizRegistrationNum())
                    .bizStatus(business.getBizStatus())
                    .bizType(business.getBizType())
                    .faxNum(business.getFaxNum())
                    .managerName(business.getManagerName())
                    .managerPhoneNum(business.getManagerPhoneNum())
                    .build();

            return MemberResponse.builder()
                    .memberId(business.getId())
                    .role(business.getRole())
                    .loginId(business.getLoginId())
                    .name(business.getBizName())
                    .email(business.getEmail())
                    .phoneNum(business.getPhoneNum())
                    .emailNotice(consent.getEmailNotice())
                    .smsNotice(consent.getSmsNotice())
                    .kakaoNotice(consent.getKakaoNotice())
                    .zipcode(adr.getZipcode())
                    .address(adr.getAddress())
                    .detailedAddress(adr.getDetailedAddress())
                    .agreements(terms)
                    .bizInfo(bizInfoResponse)
                    .memo(business.getAdminMemo())
                    .build();
        } else {
            throw new CustomException(ErrorCode.INVALID_ROLE);
        }
    }

    @Transactional
    public void updateMemo(CustomUserDetails customUserDetails , MemoUpdateRequest request, HttpServletRequest httpServletRequest){
        globalService.validateAdmin(customUserDetails);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.updateMemo(request.getAdminMemo(), customUserDetails.getId());

        globalService.log(
                customUserDetails.getId(),
                customUserDetails.getUsername(),
                ActionType.UPDATE,
                httpServletRequest.getRequestURI(),
                "user",
                request.getUserId(),
                httpServletRequest.getRemoteAddr(),
                "회원 세부 정보 조회 후 관리자 메모 수정"
                );
    }

    public SearchResult<MemberSearchResponse> searchMember(CustomUserDetails customUserDetails, MemberSearchRequest request, Pageable pageable) {
        globalService.validateAdmin(customUserDetails);

        QUser user = QUser.user;
        QConsumer consumer = QConsumer.consumer;
        QBusiness business = QBusiness.business;
        QOrder orders = QOrder.order;
        QUserMKConsent userMKConsent = QUserMKConsent.userMKConsent;
        BooleanBuilder builder = new BooleanBuilder();


        if (StringUtils.hasText(request.getKeyword()) && request.getKey() != null) {
            switch (request.getKey()) {
                case NAME -> builder.and(consumer.name.containsIgnoreCase(request.getKeyword()));
                case LOGIN_ID -> builder.and(user.loginId.containsIgnoreCase(request.getKeyword()));
                case PHONE_NUM -> builder.and(user.phoneNum.containsIgnoreCase(request.getKeyword()));
                case EMAIL -> builder.and(user.email.containsIgnoreCase(request.getKeyword()));
                case BIZ_NAME -> builder.and(business.bizName.containsIgnoreCase(request.getKeyword()));
                case BIZ_REGISTER_NUM ->
                        builder.and(business.bizRegistrationNum.containsIgnoreCase(request.getKeyword()));
            }
        }

        if (request.getRole() != null) {
            if (request.getRole() == Role.ALL) {
                BooleanBuilder roleBuilder = new BooleanBuilder();
                roleBuilder.or(user.role.eq(Role.CONSUMER));
                roleBuilder.or(user.role.eq(Role.BUSINESS));
                builder.and(roleBuilder);
            } else if (request.getRole() == Role.BUSINESS || request.getRole() == Role.CONSUMER) {
                builder.and(user.role.eq(request.getRole()));
            } else {
                throw new CustomException(ErrorCode.INVALID_ROLE);
            }
        }

        if(request.getStatus() != null){
            switch (request.getStatus()) {
                case ACTIVE -> builder.and(user.status.eq(UserStatus.ACTIVE));
                case DORMANT -> {
                    BooleanBuilder statusBuilder = new BooleanBuilder();
                    statusBuilder.or(user.status.eq(UserStatus.DORMANT));
                    statusBuilder.or(user.status.eq(UserStatus.ADMIN_DORMANT));
                    builder.and(statusBuilder);
                }
                case ALL -> {
                    BooleanBuilder allBuilder = new BooleanBuilder();
                    allBuilder.or(user.status.eq(UserStatus.DORMANT));
                    allBuilder.or(user.status.eq(UserStatus.ADMIN_DORMANT));
                    allBuilder.or(user.status.eq(UserStatus.ACTIVE));
                    builder.and(allBuilder);
                }
            }
        }

        if (request.getSignUpDate() != null) {
            if (request.getSignUpDate().getStartDate() != null) {
                builder.and(user.createdAt.goe(request.getSignUpDate().getStartDate().atStartOfDay()));
            }
            if (request.getSignUpDate().getEndDate() != null) {
                builder.and(user.createdAt.loe(request.getSignUpDate().getEndDate().atTime(23, 59, 59)));
            }
        }

        if (request.getLastLoginDate() != null) {
            if (request.getLastLoginDate().getStartDate() != null) {
                builder.and(user.lastLoginAt.goe(request.getLastLoginDate().getStartDate().atStartOfDay()));
            }
            if (request.getLastLoginDate().getEndDate() != null) {
                builder.and(user.lastLoginAt.loe(request.getLastLoginDate().getEndDate().atTime(23, 59, 59)));
            }
        }

        if (request.getMileage() != null) {
            if (request.getMileage().getStart() != null) {
                builder.and(user.mileage.goe(request.getMileage().getStart()));
            }
            if (request.getMileage().getEnd() != null){
                builder.and(user.mileage.loe(request.getMileage().getEnd()));
            }
        }

        if(request.getEmailNotice() != null){
            builder.and(
                    JPAExpressions.selectOne()
                            .from(userMKConsent)
                            .where(
                                    userMKConsent.user.id.eq(user.id)
                                            .and(userMKConsent.emailNotice.eq(request.getEmailNotice()))
                            )
                            .exists()
            );
        }

        if(request.getSmsNotice() != null){
            builder.and(
                    JPAExpressions.selectOne()
                            .from(userMKConsent)
                            .where(
                                    userMKConsent.user.id.eq(user.id)
                                            .and(userMKConsent.smsNotice.eq(request.getSmsNotice()))
                            )
                            .exists()
            );
        }

        if(request.getKakaoNotice() != null){
            builder.and(
                    JPAExpressions.selectOne()
                            .from(userMKConsent)
                            .where(
                                    userMKConsent.user.id.eq(user.id)
                                            .and(userMKConsent.kakaoNotice.eq(request.getKakaoNotice()))
                            )
                            .exists()
            );
        }

        if(request.getProvider() != null){
            builder.and(user.provider.eq(request.getProvider()));
        }

        Integer min = request.getOrderCount() != null ? request.getOrderCount().getStart() : null;
        Integer max = request.getOrderCount() != null ? request.getOrderCount().getEnd() : null;

        Expression<Long> orderCountExpr =
                Expressions.template(Long.class, "coalesce(count({0}), 0)", orders.id);

        JPQLQuery<MemberSearchResponse> query = jpaQueryFactory
                .select(Projections.constructor(MemberSearchResponse.class,
                        user.id,
                        user.loginId,
                        user.userName,
                        user.role,
                        user.provider,
                        user.mileage,
                        orderCountExpr,
                        user.phoneNum,
                        user.createdAt,
                        user.lastLoginAt,
                        user.status
                ))
                .from(user)
                .leftJoin(consumer).on(consumer.id.eq(user.id))
                .leftJoin(business).on(business.id.eq(user.id))
                .leftJoin(userMKConsent).on(userMKConsent.user.id.eq(user.id))
                .leftJoin(orders).on(orders.userId.eq(user.id))
                .where(builder)
                .groupBy(user.id, consumer.name, business.bizName, user.phoneNum,
                        user.loginId, user.role, user.provider, user.mileage,
                        user.createdAt, user.lastLoginAt, user.status)
                .orderBy(user.createdAt.desc());


        if (min != null && max != null) {
            query.having(orders.count().between(min.longValue(), max.longValue()));
        } else if (min != null) {
            query.having(orders.count().goe(min.longValue()));
        } else if (max != null) {
            query.having(orders.count().loe(max.longValue()));
        }

        List<MemberSearchResponse> responses = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(user.id.countDistinct())
                .from(user)
                .leftJoin(orders).on(orders.userId.eq(user.id))
                .where(builder)
                .groupBy(user.id);

        long totalCount = countQuery.fetch().size();


        return SearchResult.<MemberSearchResponse> builder()
                .totalCount(totalCount)
                .size(pageable.getPageSize())
                .page(pageable.getPageNumber())
                .data(responses)
                .build();
    }

    @Transactional
    public List<StatusResponse> updateStatus(CustomUserDetails customUserDetails, List<Long> idList, String status, HttpServletRequest request){
        globalService.validateAdmin(customUserDetails);

        List<User> userList = userRepository.findAllById(idList);

        UserStatus userStatus = validateStatus(status);

        String ipAddress = request.getRemoteAddr();

        userList.stream()
                .map(user -> {
                    user.updateStatusByAdmin(userStatus, customUserDetails.getId());
                    UserStatusLog log = UserStatusLog.builder()
                            .updateStatus(userStatus)
                            .agent(customUserDetails.getUsername())
                            .reason(status + "상태로 관리자가 설정")
                            .withdrawIpAdress(ipAddress)
                            .createdBy(customUserDetails.getId())
                            .user(user)
                            .build();

                    user.getUserStatusLogs().add(log);
                    return log;

                }).collect(Collectors.toList());

        List<User> saved = userRepository.saveAll(userList);

        idList.forEach(id ->
                globalService.log(
                        customUserDetails.getId(),
                        customUserDetails.getUsername(),
                        ActionType.UPDATE,
                        request.getRequestURI(),
                        "user",
                        id,
                        request.getRemoteAddr(),
                        "회원 상태 변경 : " + status
                )
        );

        return saved.stream()
                .map(user -> StatusResponse.builder()
                        .userId(user.getId())
                        .loginId(user.getLoginId())
                        .status(user.getStatus())
                        .updatedAt(user.getUpdatedAt())
                        .build()
                ).toList();
    }

    public List<MileageUpdateResponse> updateMileage(CustomUserDetails customUserDetails, MileageUpdateRequest request, HttpServletRequest httpServletRequest){
        globalService.validateAdmin(customUserDetails);

        List<User> userList = userRepository.findAllById(request.getIdList());
        Integer amt;

        if(request.getType() == MileageType.GRANT_BY_ADMIN){
            amt = request.getAmount();
        } else if (request.getType() != MileageType.DEDUCT_BY_ADMIN) {
            amt = -request.getAmount();
        } else {
            throw new CustomException(ErrorCode.INVALID_TYPE);
        }

        userList.stream()
                .map(user -> {
                    user.updateMileageByAdmin(amt, customUserDetails.getId());
                    MileageLog log = MileageLog.builder()
                            .type(request.getType())
                            .name(user.getUserName())
                            .amount(amt)
                            .adminMemo(request.getAdminMemo())
                            .agent(customUserDetails.getUsername())
                            .user(user)
                            .build();
                    user.getMileageLogs().add(log);
                    return log;

                }).collect(Collectors.toList());

        userRepository.saveAll(userList);

        request.getIdList().forEach(id ->
                globalService.log(
                        customUserDetails.getId(),
                        customUserDetails.getUsername(),
                        ActionType.UPDATE,
                        httpServletRequest.getRequestURI(),
                        "user",
                        id,
                        httpServletRequest.getRemoteAddr(),
                        "회원 마일리지 지급 및 차감"
                )
        );

        return userList.stream()
                .map(user -> MileageUpdateResponse.builder()
                        .targetId(user.getId())
                        .afterMileage(user.getMileage())
                        .type(request.getType())
                        .memo(request.getAdminMemo())
                        .updatedAt(user.getUpdatedAt())
                        .build()
                ).collect(Collectors.toList());

    }

    public List<MileageLogResponse> getMileageLog(CustomUserDetails customUserDetails , Long userId, HttpServletRequest request){
        globalService.validateAdmin(customUserDetails);
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<MileageType> types = List.of(MileageType.ORDER_USAGE, MileageType.ORDER_REFUND);

        globalService.log(
                customUserDetails.getId(),
                customUserDetails.getUsername(),
                ActionType.UPDATE,
                request.getRequestURI(),
                "user",
                userId,
                request.getRemoteAddr(),
                "회원 세부 정보 조회 후 마일리지 사용 내역 조회"
                );

        return mileageLogRepository.findByUserIdAndTypeIn(userId, types).stream()
                .map(l -> {
                    String description = switch (l.getType()) {
                        case ORDER_USAGE -> "주문 시 사용한 마일리지입니다.";
                        case ORDER_REFUND -> "주문 취소로 인해 반환된 마일리지입니다.";
                        default -> throw new CustomException(ErrorCode.INVALID_TYPE);
                    };

                    return MileageLogResponse.builder()
                            .loginId(user.getLoginId())
                            .type(l.getType())
                            .amount(l.getAmount())
                            .description(description)
                            .useTime(l.getCreatedAt())
                            .build();
                }).collect(Collectors.toList());

    }

    public List<UserCouponResponse> getUserCoupon(CustomUserDetails customUserDetails, Long userId, HttpServletRequest request){
        globalService.validateAdmin(customUserDetails);

        globalService.log(
                customUserDetails.getId(),
                customUserDetails.getUsername(),
                ActionType.GET,
                request.getRequestURI(),
                "user_coupon",
                userId,
                request.getRemoteAddr(),
                "회원 세부 정보 조회 후 쿠폰 보유 내역 조회"
                );

        return userCouponRepository.findAllByUserId(userId).stream()
                .map(c -> {
                    LocalDate today = LocalDate.now();
                    Coupon coupon = c.getCoupon();
                    Integer expireDate;
                    boolean isExpired;

                    if (coupon.getCouponExpiration() == CouponExpiration.ISSUED_DATE) {
                        LocalDate issuedDate = c.getCreatedAt().toLocalDate();
                        LocalDate expireAt = issuedDate.plusDays(coupon.getIssuedDate());
                        expireDate = (int) ChronoUnit.DAYS.between(today, expireAt);
                    } else if (coupon.getCouponExpiration() == CouponExpiration.FIX_DATE) {
                        expireDate = (int) ChronoUnit.DAYS.between(today, coupon.getExpireEndDate());
                    } else {
                        throw new CustomException(ErrorCode.INVALID_TYPE);
                    }

                    isExpired = expireDate < 0;

                    return UserCouponResponse.builder()
                            .couponId(coupon.getId())
                            .isExpired(isExpired)
                            .couponType(coupon.getCouponType())
                            .couponName(coupon.getCouponName())
                            .couponExpiration(coupon.getCouponExpiration())
                            .expireEndDate(coupon.getExpireEndDate())
                            .issuedDate(expireDate)
                            .couponDiscountType(coupon.getCouponDiscountType())
                            .discountPrice(coupon.getDiscountPrice())
                            .build();
                }).collect(Collectors.toList());
    }

    public List<CouponUseLogResponse> getCouponLog(CustomUserDetails customUserDetails, Long userId, HttpServletRequest request){
        globalService.validateAdmin(customUserDetails);

        List<CouponLog> logs = couponLogRepository.findAllByUserIdAndStatusIn(userId, List.of(CouponStatus.USED, CouponStatus.REFUNDED));

        globalService.log(
                customUserDetails.getId(),
                customUserDetails.getUsername(),
                ActionType.GET,
                request.getRequestURI(),
                "user_coupon_use_log",
                userId,
                request.getRemoteAddr(),
                "회원 세부 정보 조회 후 쿠폰 사용 내역 조회"
                );

        return logs.stream()
                .map(l -> CouponUseLogResponse.builder()
                        .couponId(l.getCoupon().getId())
                        .status(l.getStatus())
                        .discountPrice(l.getDiscountPrice())
                        .description(l.getMemo())
                        .logTime(l.getCreatedAt())
                        .build()
                ).collect(Collectors.toList());
    }

    public SearchResult<DormantSearchResponse> searchDormantMember(CustomUserDetails customUserDetails, DormantSearchRequest request, Pageable pageable) {
        globalService.validateAdmin(customUserDetails);

        QUser user = QUser.user;
        QConsumer consumer = QConsumer.consumer;
        QBusiness business = QBusiness.business;
        BooleanBuilder builder = new BooleanBuilder()
                .and(user.role.in(Role.CONSUMER, Role.BUSINESS))
                .and(user.status.in(UserStatus.DORMANT, UserStatus.ADMIN_DORMANT));

        if (StringUtils.hasText(request.getKeyword()) && request.getKey() != null) {
            switch (request.getKey()) {
                case NAME -> builder.and(user.userName.containsIgnoreCase(request.getKeyword()));
                case LOGIN_ID -> builder.and(user.loginId.containsIgnoreCase(request.getKeyword()));
            }
        }

        if(request.getTransitionDate() != null){
            if(request.getTransitionDate().getStartDate() != null){
                builder.and(user.dormantAt.goe(request.getTransitionDate().getStartDate().atStartOfDay()));
            }
            if(request.getTransitionDate().getEndDate() != null){
                builder.and(user.dormantAt.loe(request.getTransitionDate().getEndDate().atTime(23,59,59)));
            }
        }

        List<DormantSearchResponse> results = jpaQueryFactory
                .select(Projections.constructor(DormantSearchResponse.class,
                        user.id,
                        user.loginId,
                        user.userName,
                        user.role,
                        user.provider,
                        user.mileage,
                        user.phoneNum,
                        user.createdAt,
                        user.dormantAt
                ))
                .from(user)
                .leftJoin(consumer).on(consumer.id.eq(user.id))
                .leftJoin(business).on(business.id.eq(user.id))
                .where(builder)
                .orderBy(user.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = jpaQueryFactory
                .select(user.count())
                .from(user)
                .where(builder)
                .fetchOne();

        return SearchResult.<DormantSearchResponse>builder()
                .totalCount(totalCount != null ? totalCount : 0L)
                .size(pageable.getPageSize())
                .page(pageable.getPageNumber())
                .data(results)
                .build();

    }

    public SearchResult<DeleteSearchResponse> searchDeleteMember(CustomUserDetails customUserDetails, DeleteSearchRequest request, Pageable pageable) {
        globalService.validateAdmin(customUserDetails);

        QUser user = QUser.user;
        QUserStatusLog statusLog = QUserStatusLog.userStatusLog;
        BooleanBuilder builder = new BooleanBuilder()
                .and(user.role.in(Role.CONSUMER, Role.BUSINESS))
                .and(user.status.in(UserStatus.DELETED, UserStatus.ADMIN_DELETED));

        if (StringUtils.hasText(request.getKeyword()) && request.getKey() != null) {
            switch (request.getKey()){
                case LOGIN_ID -> builder.and(user.loginId.containsIgnoreCase(request.getKeyword()));
                case NAME -> builder.and(user.userName.containsIgnoreCase(request.getKeyword()));
            }

        }

        if(request.getDeleteDate() != null){
            if(request.getDeleteDate().getStartDate() != null){
                builder.and(user.dormantAt.goe(request.getDeleteDate().getStartDate().atStartOfDay()));
            }
            if(request.getDeleteDate().getEndDate() != null){
                builder.and(user.dormantAt.loe(request.getDeleteDate().getEndDate().atTime(23,59,59)));
            }
        }

        if(request.getDeleteByAdmin() != null){
            if(request.getDeleteByAdmin() == true){
                builder.and(user.status.eq(UserStatus.ADMIN_DELETED));
            }
            if(request.getDeleteByAdmin() == false){
                builder.and(user.status.eq(UserStatus.DELETED));
            }
        }

        if(request.getRecoverable() != null){
            builder.and(user.recoverable.eq(request.getRecoverable()));
        }

        List<User> users = jpaQueryFactory
                .selectFrom(user)
                .leftJoin(user.userStatusLogs, statusLog).fetchJoin()
                .where(builder)
                .orderBy(user.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<DeleteSearchResponse> responses = users.stream()
                .map(u -> {
                    UserStatusLog log = u.getUserStatusLogs().stream()
                            .findFirst()
                            .orElse(null);

                    String withdrawIp = null;
                    String agent = null;
                    String reason =  null;

                    if(log != null){
                        withdrawIp = log.getWithdrawIpAdress();
                        agent = log.getAgent();
                        reason = log.getReason();
                    }

                    return DeleteSearchResponse.builder()
                            .userId(u.getId())
                            .loginId(u.getLoginId())
                            .role(u.getRole())
                            .deleteType(u.getStatus())
                            .deleteAt(u.getDeletedAt() != null ? u.getDeletedAt().toLocalDate() : null)
                            .recoverable(u.getRecoverable())
                            .withdrawIp(withdrawIp)
                            .agent(agent)
                            .reason(reason)
                            .build();
                }).collect(Collectors.toList());

        Long totalCount = jpaQueryFactory
                .select(user.count())
                .from(user)
                .where(builder)
                .fetchOne();

        return SearchResult.<DeleteSearchResponse>builder()
                .totalCount(totalCount != null ? totalCount : 0L)
                .size(pageable.getPageSize())
                .page(pageable.getPageNumber())
                .data(responses)
                .build();

    }

    private UserStatus validateStatus(String status){
        try {
            return UserStatus.valueOf(status.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new CustomException(ErrorCode.INVALID_USER_STATUS);
        }
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



    @Transactional
    public void processUserExcel(CustomUserDetails customUserDetails, MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {

            // Sheet 1: 일반 사용자
            Sheet consumerSheet = workbook.getSheetAt(0);
            for (int i = 1; i <= consumerSheet.getLastRowNum(); i++) {
                Row row = consumerSheet.getRow(i);
                if (row == null) continue;

                String name = getString(row.getCell(0));
                String phone = getString(row.getCell(1));
                String email = getString(row.getCell(2));

                String loginId = generateUniqueLoginId("consumer");

                User user = User.builder()
                        .loginId(loginId)
                        .email(email)
                        .role(Role.CONSUMER)
                        .status(UserStatus.ACTIVE)
                        .build();

                Consumer consumer = Consumer.builder()
                        .name(name)
                        .phoneNum(phone)
                        .build();

                userRepository.save(user);
                consumerRepository.save(consumer);
            }

            // Sheet 2: 사업자 사용자
            Sheet businessSheet = workbook.getSheetAt(1);
            for (int i = 1; i <= businessSheet.getLastRowNum(); i++) {
                Row row = businessSheet.getRow(i);
                if (row == null) continue;

                String ownerName = getString(row.getCell(0));
                String businessName = getString(row.getCell(1));
                String bizNum = getString(row.getCell(2));
                String phone = getString(row.getCell(3));
                String email = getString(row.getCell(4));

                String loginId = generateUniqueLoginId("biz");

                Business business = Business.builder()
                        .loginId(loginId)
                        .email(email)
                        .role(Role.BUSINESS)
                        .status(UserStatus.ACTIVE)
                        .ownerName(ownerName)
                        .bizName(businessName)
                        .phoneNum(phone)
                        .bizRegistrationNum(bizNum)
                        .build();

                businessRepository.save(business);
            }

        } catch (IOException e) {
            throw new CustomException(ErrorCode.INVALID_EXCEL_FILE);
        }
    }

    private String getString(Cell cell) {
        if (cell == null) return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    private String generateUniqueLoginId(String prefix) {
        String date = LocalDate.now().format(DATE_FORMAT);
        int attempt = 1;
        String candidate;

        do {
            candidate = String.format("%s%s_%03d", prefix, date, attempt++);
        } while (userRepository.existsByLoginId(candidate));

        return candidate;
    }
}
