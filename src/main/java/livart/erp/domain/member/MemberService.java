package livart.erp.domain.member;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import livart.common.Auth.CustomUserDetails;
import livart.common.domain.address.entity.UserAddress;
import livart.common.domain.alarm.entity.QUserMKConsent;
import livart.common.domain.alarm.entity.UserMKConsent;
import livart.common.domain.order.entity.QOrder;
import livart.common.domain.term.entity.Term;
import livart.common.domain.term.entity.UserTerm;
import livart.common.domain.term.repository.TermRepository;
import livart.common.domain.user.entity.*;
import livart.common.domain.user.repository.BusinessRepository;
import livart.common.domain.user.repository.ConsumerRepository;
import livart.common.domain.user.repository.UserRepository;
import livart.common.log.repository.UserStatusLogRepository;
import livart.common.dto.enums.user.MileageType;
import livart.common.dto.enums.user.Provider;
import livart.common.dto.enums.user.Role;
import livart.common.dto.enums.user.UserStatus;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.log.entity.MileageLog;
import livart.common.log.entity.UserStatusLog;
import livart.common.mapper.SearchResult;
import livart.common.service.GlobalService;
import livart.erp.domain.member.dto.request.MemberRequest;
import livart.erp.domain.member.dto.request.MemberSearchRequest;
import livart.erp.domain.member.dto.request.MileageUpdateRequest;
import livart.erp.domain.member.dto.response.MemberSearchResponse;
import livart.erp.domain.member.dto.response.MileageUpdateResponse;
import livart.erp.domain.member.dto.response.StatusResponse;
import livart.shop.security.dto.response.SignupResponse;
import lombok.RequiredArgsConstructor;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class MemberService {
    private final GlobalService globalService;
    private final JPAQueryFactory jpaQueryFactory;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final ConsumerRepository consumerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TermRepository termRepository;
    private final UserStatusLogRepository userStatusLogRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Transactional
    public SignupResponse registerMember(CustomUserDetails customUserDetails, MemberRequest request) {
        globalService.validateAdmin(customUserDetails);

        validateLoginId(request.getLoginId());
        validatePassword(request.getPassword());

        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        User user = User.builder()
                .loginId(request.getLoginId())
                .password(encodedPassword)
                .email(request.getEmail())
                .role(request.getRole())
                .provider(Provider.LOCAL)
                .status(UserStatus.ACTIVE)
                .adminRegister(true)
                .build();

        UserAddress address = UserAddress.builder()
                .zipcode(request.getZipcode())
                .address(request.getAddress())
                .detailedAddress(request.getDetailedAddress())
                .defaultAddress(true)
                .user(user)
                .build();

        List<UserTerm> userTerms = request.getAgreements()
                .stream()
                .map(agreement -> {
                    Term term = termRepository.findById(agreement.getTermsId())
                            .orElseThrow(() -> new CustomException(ErrorCode.TERM_NOT_FOUND));

                    return UserTerm.builder()
                            .isAgreed(agreement.getIsAgreed())
                            .user(user)
                            .term(term)
                            .build();
                })
                .collect(Collectors.toList());

        UserMKConsent userMKConsent = UserMKConsent.builder()
                .emailNotice(request.getEmailNotice())
                .smsNotice(request.getSmsNotice())
                .kakaoNotice(request.getKakaoNotice())
                .user(user)
                .build();

        user.getUserAddresses().add(address);
        user.getUserTerms().addAll(userTerms);
        user.getUserMarketingNotices().add(userMKConsent);
        User saved = userRepository.save(user);

        if (request.getRole() == Role.CONSUMER) {
            Consumer consumer = Consumer.ConsumerFromUser(saved, request.getName(), request.getPhoneNum());
            Consumer saveConsumer = consumerRepository.save(consumer);

            return SignupResponse.builder()
                    .loginId(saveConsumer.getLoginId())
                    .userName(saveConsumer.getName())
                    .email(saveConsumer.getEmail())
                    .role(saveConsumer.getRole())
                    .createdAt(saveConsumer.getConsCreatedAt())
                    .build();

        } else if (request.getRole() == Role.BUSINESS) {
            Business business = Business.businessFromUser(saved, request.getPresidentName(), request.getBizName(), request.getPhoneNum(), request.getBizRegisterationName(), request.getBizStatus(), request.getBizType(), request.getFaxNum(), request.getManagerName(), request.getManagerPhoneNum());
            Business saveBusiness = businessRepository.save(business);

            return SignupResponse.builder()
                    .loginId(saveBusiness.getLoginId())
                    .userName(saveBusiness.getBizName())
                    .email(saveBusiness.getEmail())
                    .role(saveBusiness.getRole())
                    .createdAt(saveBusiness.getBizCreatedAt())
                    .build();
        } else {
            throw new CustomException(ErrorCode.INVALID_ROLE); // 명확하게 처리
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
                case PHONE_NUM -> {
                    BooleanBuilder phoneBuilder = new BooleanBuilder();
                    phoneBuilder.and(consumer.phoneNum.containsIgnoreCase(request.getKeyword()));
                    phoneBuilder.and(business.bizPhoneNum.containsIgnoreCase(request.getKeyword()));
                    builder.and(phoneBuilder);
                }
                case EMAIL -> builder.and(user.email.containsIgnoreCase(request.getKeyword()));
                case BIZ_NAME -> builder.and(business.bizPhoneNum.containsIgnoreCase(request.getKeyword()));
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

        if (request.getStatus() != null) {
            if (request.getStatus() == UserStatus.DELETED) {
                throw new CustomException(ErrorCode.INVALID_USER_STATUS);
            } else {
                builder.and(user.status.eq(request.getStatus()));
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
            builder.and(userMKConsent.emailNotice.eq(request.getEmailNotice()));
        }

        if(request.getSmsNotice() != null){
            builder.and(userMKConsent.smsNotice.eq(request.getSmsNotice()));
        }

        if(request.getKakaoNotice() != null){
            builder.and(userMKConsent.kakaoNotice.eq(request.getKakaoNotice()));
        }

        if(request.getProvider() != null){
            builder.and(user.provider.eq(request.getProvider()));
        }

        Integer min = request.getOrderCount() != null ? request.getOrderCount().getStart() : null;
        Integer max = request.getOrderCount() != null ? request.getOrderCount().getEnd() : null;

        NumberExpression<Long> orderCountExpr = orders.id.count();

        JPQLQuery<MemberSearchResponse> query = jpaQueryFactory
                .select(Projections.constructor(MemberSearchResponse.class,
                        user.id,
                        user.loginId,
                        Expressions.stringTemplate(
                                "COALESCE(CASE WHEN {0} = 'CONSUMER' THEN {1} ELSE {2} END, '')",
                                user.role, consumer.name, business.bizName
                        ).as("userName"),
                        user.role,
                        user.provider,
                        user.mileage,
                        orderCountExpr.as("orderCount"),
                        Expressions.stringTemplate(
                                "CASE WHEN {0} = 'CONSUMER' THEN {1} ELSE {2} END",
                                user.role, consumer.phoneNum, business.bizPhoneNum
                        ).as("phoneNum"),
                        Expressions.dateTemplate(LocalDate.class, "DATE({0})", user.createdAt),
                        Expressions.dateTemplate(LocalDate.class, "DATE({0})", user.lastLoginAt),
                        user.status
                ))
                .from(user)
                .leftJoin(consumer).on(consumer.id.eq(user.id))
                .leftJoin(business).on(business.id.eq(user.id))
                .leftJoin(userMKConsent).on(userMKConsent.user.id.eq(user.id))
                .leftJoin(orders).on(orders.userId.eq(user.id))
                .where(builder)
                .groupBy(user.id, consumer.name, business.bizName,
                        consumer.phoneNum, business.bizPhoneNum,
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

        UserStatus updateStatus = validateStatus(status);

        String ipAddress = request.getRemoteAddr();

        userList.stream()
                .map(user -> {
                    user.updateStatusByAdmin(updateStatus, customUserDetails.getId());
                    UserStatusLog log = UserStatusLog.builder()
                            .updateStatus(updateStatus)
                            .byAdmin(true)
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

        return saved.stream()
                .map(user -> StatusResponse.builder()
                        .userId(user.getId())
                        .loginId(user.getLoginId())
                        .status(user.getStatus())
                        .updatedAt(user.getUpdatedAt())
                        .build()
                ).toList();
    }

    public List<MileageUpdateResponse> updateMileage(CustomUserDetails customUserDetails, MileageUpdateRequest request){
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
                            .amount(amt)
                            .adminMemo(request.getAdminMemo())
                            .performerId(customUserDetails.getId())
                            .user(user)
                            .build();
                    user.getMileageLogs().add(log);
                    return log;

                }).collect(Collectors.toList());

        userRepository.saveAll(userList);

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

    private UserStatus validateStatus(String status){
        try {
            return UserStatus.valueOf(status.toUpperCase());
        }catch (IllegalArgumentException e){
            throw new CustomException(ErrorCode.INVALID_USER_STATUS);
        }
    }

    @Transactional
    public void processUserExcel(MultipartFile file) {
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

                User user = User.builder()
                        .loginId(loginId)
                        .email(email)
                        .role(Role.BUSINESS)
                        .status(UserStatus.ACTIVE)
                        .build();

                Business business = Business.builder()
                        .ownerName(ownerName)
                        .bizName(businessName)
                        .bizPhoneNum(phone)
                        .bizRegistrationNum(bizNum)
                        .build();

                userRepository.save(user);
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
