package livart.erp.domain.defaultSetting.admin;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpSession;
import livart.common.Auth.CustomUserDetails;
import livart.common.domain.setting.entity.AllowedAdminIp;
import livart.common.domain.setting.repository.AllowedAdminIpsRepository;
import livart.common.domain.user.entity.Admin;
import livart.common.domain.user.entity.QAdmin;
import livart.common.domain.user.entity.User;
import livart.common.domain.user.repository.AdminRepository;
import livart.common.domain.user.repository.UserRepository;
import livart.common.dto.enums.*;
import livart.common.dto.enums.user.Provider;
import livart.common.dto.enums.user.Role;
import livart.common.dto.enums.user.UserStatus;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.log.entity.AdminActionLog;
import livart.common.log.entity.OtpLog;
import livart.common.log.repository.AdminActionLogRepository;
import livart.common.log.repository.LoginHistoryRepository;
import livart.common.log.repository.OtpLogRepository;
import livart.common.service.GlobalService;
import livart.common.mapper.SearchResult;
import livart.erp.domain.defaultSetting.admin.dto.request.AdminLogSearchRequest;
import livart.erp.domain.defaultSetting.admin.dto.request.AdminRequest;
import livart.erp.domain.defaultSetting.admin.dto.request.AdminSearchRequest;
import livart.erp.domain.defaultSetting.admin.dto.response.*;
import livart.erp.domain.defaultSetting.admin.enums.AdminSearchKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final GlobalService globalService;
    private final AdminRegisterMapper adminRegisterMapper;
    private final AdminSearchMapper adminSearchMapper;
    private final AllowedAdminIpsRepository allowedAdminIpsRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AdminActionLogRepository adminActionLogRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final OtpLogRepository otpLogRepository;

    @Transactional
    public AdminResponse createAdmin(CustomUserDetails customUserDetails, AdminRequest request, HttpSession session){

        if(request.getIpList() == null){
            throw new CustomException(ErrorCode.NULL_INPUT_IP_LIST);
        }

        if(!customUserDetails.getRole().equals(Role.SUPER_ADMIN)){
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        validateLoginId(customUserDetails, request.getLoginId());
        validatePassword(request.getPassword());

        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        Optional<OtpLog> verifiedOtp = otpLogRepository.findTopByPhoneNumAndStatusOrderBySentAtDesc(request.getPhoneNum(), OtpStatus.VERIFIED);

        if (verifiedOtp.isEmpty() || verifiedOtp.get().getSentAt().isBefore(LocalDateTime.now().minus(30, ChronoUnit.MINUTES))) {
            throw new CustomException(ErrorCode.PHONE_AUTHORIZED);
        }

        Admin admin = adminRegisterMapper.toEntity(request).toBuilder()
                .password(encodedPassword)
                .role(Role.ADMIN)
                .provider(Provider.LOCAL)
                .status(UserStatus.ACTIVE)
                .adminRegister(true)
                .phoneNum(request.getPhoneNum())
                .mileage(0)
                .userName(request.getAdminName())
                .updatedBy(customUserDetails.getId())
                .build();

        Admin saved1 = adminRepository.save(admin);

        if(request.getLimitIpAccess() == true){
            List<AllowedAdminIp> allowedAdminIpList = request.getIpList()
                    .stream()
                    .map(ip -> AllowedAdminIp.builder()
                            .admin(saved1)
                            .ipAddress(ip)
                            .build())
                    .collect(Collectors.toList());
            saved1.getAllowedAdminIps().addAll(allowedAdminIpList);
        }

        Admin saved = adminRepository.save(saved1);
        session.removeAttribute("PHONE_VERIFIED_" + request.getPhoneNum());

        List<String> ipList = saved.getAllowedAdminIps().stream()
                .map(AllowedAdminIp::getIpAddress)
                .collect(Collectors.toList());

        AdminResponse response = adminRegisterMapper.toDto(saved)
                .toBuilder()
                .adminId(saved.getId())
                .limitIpAccess(request.getLimitIpAccess())
                .ipList(ipList)
                .build();

        return response;

    }

    public AdminResponse getAdmin(CustomUserDetails customUserDetails, Long adminId){
        User user = globalService.validateAdmin(customUserDetails);
        if(!user.getRole().equals(Role.SUPER_ADMIN)){
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<AllowedAdminIp> ips = allowedAdminIpsRepository.findByAdminId(adminId);

        List<String> ipList = ips.stream()
                .map(AllowedAdminIp::getIpAddress)
                .collect(Collectors.toList());

        boolean limitIpAccess = !ipList.isEmpty();

        AdminResponse response = adminRegisterMapper.toDto(admin)
                .toBuilder()
                .adminId(admin.getId())
                .limitIpAccess(limitIpAccess)
                .ipList(ipList)
                .build();

        return response;
    }

    @Transactional
    public AdminResponse updateAdmin(CustomUserDetails customUserDetails, AdminRequest request, Long adminId, HttpSession session){

        if(request.getIpList() == null){
            throw new CustomException(ErrorCode.NULL_INPUT_IP_LIST);
        }

        if(!customUserDetails.getRole().equals(Role.SUPER_ADMIN)){
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        validatePassword(request.getPassword());

        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(admin.getPhoneNum() != request.getPhoneNum()){
            Optional<OtpLog> verifiedOtp = otpLogRepository.findTopByPhoneNumAndStatusOrderBySentAtDesc(request.getPhoneNum(), OtpStatus.VERIFIED);

            if (verifiedOtp.isEmpty() || verifiedOtp.get().getSentAt().isBefore(LocalDateTime.now().minus(30, ChronoUnit.MINUTES))) {
                throw new CustomException(ErrorCode.PHONE_AUTHORIZED);
            }
        }

        updateLoginId(customUserDetails, request.getLoginId(), admin);
        admin.updateFrom(request, encodedPassword);
        admin.update(encodedPassword);

        admin.getAllowedAdminIps().clear();

        if(request.getLimitIpAccess()){
            List<AllowedAdminIp> allowedAdminIpList = request.getIpList()
                    .stream()
                    .map(ip -> AllowedAdminIp.builder()
                            .admin(admin)
                            .ipAddress(ip)
                            .build())
                    .collect(Collectors.toList());

            admin.getAllowedAdminIps().addAll(allowedAdminIpList);
            allowedAdminIpsRepository.saveAll(allowedAdminIpList);
        }

        Admin saved = adminRepository.save(admin);
        session.removeAttribute("PHONE_VERIFIED_" + request.getPhoneNum());

        AdminResponse response = adminRegisterMapper.toDto(saved)
                .toBuilder()
                .adminId(saved.getId())
                .limitIpAccess(request.getLimitIpAccess())
                .ipList(request.getIpList())
                .build();

        return response;
    }

    public SearchResult<AdminSearchResponse> getAdminList(CustomUserDetails customUserDetails, AdminSearchRequest request, Pageable pageable){

        globalService.validateAdmin(customUserDetails);

        QAdmin admin = QAdmin.admin;
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(request.getKeyword()) && request.getKey() != null){
            switch (request.getKey()) {
                case LOGIN_ID -> builder.and(admin._super.loginId.containsIgnoreCase(request.getKeyword()));
                case EMAIL -> builder.and(admin._super.email.containsIgnoreCase(request.getKeyword()));
                case ADMIN_NAME -> builder.and(admin.adminName.containsIgnoreCase(request.getKeyword()));
                case PHONE_NUM -> builder.and(admin.phoneNum.containsIgnoreCase(request.getKeyword()));
                case OFFICE_NUM -> builder.and(admin.officeNum.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keyword = new BooleanBuilder();
                    keyword.or(admin._super.loginId.containsIgnoreCase(request.getKeyword()));
                    keyword.or(admin._super.email.containsIgnoreCase(request.getKeyword()));
                    keyword.or(admin.adminName.containsIgnoreCase(request.getKeyword()));
                    keyword.or(admin.phoneNum.containsIgnoreCase(request.getKeyword()));
                    keyword.or(admin.officeNum.containsIgnoreCase(request.getKeyword()));
                    builder.and(keyword);
                }
            }
        }

        if(request.getSmsNotiEnabled() != null){
            builder.and(admin.smsNotiEnabled.eq(request.getSmsNotiEnabled()));
        }

        List<Admin> adminList = jpaQueryFactory
                .selectFrom(admin)
                .where(builder)
                .orderBy(admin.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = jpaQueryFactory
                .select(admin.count())
                .from(admin)
                .where(builder)
                .fetchOne();

        List<AdminSearchResponse> results = adminList.stream()
                .map(a -> {
                    AdminSearchResponse dto = adminSearchMapper.toDto(a).toBuilder()
                            .adminId(a.getId())
                            .registerdAt(a.getCreatedAt().toLocalDate())
                            .lastLogin(a.getLastLoginAt())
                            .build();
                    return dto;
                }).collect(Collectors.toList());

        return SearchResult.<AdminSearchResponse>builder()
                .totalCount(totalCount)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(results)
                .build();
    }

    @Transactional
    public List<DelAdminResponse> deleteAdmins(CustomUserDetails customUserDetails, List<Long> adminIds){
        User user = globalService.validateAdmin(customUserDetails);
        if(!user.getRole().equals(Role.SUPER_ADMIN)){
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        List<DelAdminResponse> responseList = userRepository.findAllById(adminIds).stream()
                .map(users -> {
                    users.updateStatus(UserStatus.ADMIN_DELETED);
                    User saved = userRepository.save(users);
                    return DelAdminResponse.builder()
                            .adminId(saved.getId())
                            .loginId(saved.getLoginId())
                            .role(saved.getRole())
                            .status(saved.getStatus())
                            .build();
                    }
                ).collect(Collectors.toList());

        return responseList;
    }

    @Transactional
    public List<EnableLoginResponse> blockAdmins(CustomUserDetails customUserDetails, List<Long> adminIds){
        User user = globalService.validateAdmin(customUserDetails);
        if(!user.getRole().equals(Role.SUPER_ADMIN)){
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        List<EnableLoginResponse> responseList = adminRepository.findAllById(adminIds).stream()
                .map(admin -> {
                            admin.updateLoginEnabled(false);
                            Admin saved = adminRepository.save(admin);
                            return EnableLoginResponse.builder()
                                    .adminId(saved.getId())
                                    .loginId(saved.getLoginId())
                                    .role(saved.getRole())
                                    .adminName(saved.getAdminName())
                                    .loginEnabled(saved.getLoginEnabled())
                                    .build();
                        }
                ).collect(Collectors.toList());

        return responseList;
    }

    public UnifiedLogGroupResponse getLogList(CustomUserDetails customUserDetails,
                                                       AdminLogSearchRequest request,
                                                       Pageable pageable) {

        globalService.validateAdmin(customUserDetails);

        LocalDateTime startDateTime = request.getStartDate().atStartOfDay(); // 2024-04-20T00:00:00
        LocalDateTime endDateTime = request.getEndDate().atTime(LocalTime.MAX);

        // 1. 회원 정보 열람 이력
        List<UnifiedLogResponse> memberLogs = adminActionLogRepository
                .findLogsByPartialLoginId(request.getAdminLoginId(),"user", startDateTime,endDateTime, pageable)
                .stream()
                .map(log -> toUnifiedDto(LogType.MEMBER, log))
                .collect(Collectors.toList());

        // 2. 운영자 정보 열람 이력
        List<UnifiedLogResponse> adminLogs = adminActionLogRepository
                .findLogsByPartialLoginId(request.getAdminLoginId(), "admin", startDateTime,endDateTime, pageable)
                .stream()
                .map(log -> toUnifiedDto(LogType.ADMIN, log))
                .collect(Collectors.toList());

        // 3. 로그인 기록
        List<UnifiedLogResponse> loginLogs = loginHistoryRepository
                .findByLoginIdLikeAndSuccessAndLoginAtBetween(request.getAdminLoginId(),true, startDateTime,endDateTime, pageable)
                .stream()
                .map(log -> UnifiedLogResponse.builder()
                        .logType(LogType.LOGIN)
                        .adminLoginId(log.getLoginId())
                        .actionType(ActionType.LOGIN)
                        .page("관리자 ERP 로그인")
                        .targetLoginId(log.getLoginId())
                        .ipAddress(log.getIpAddress())
                        .accessAt(log.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return UnifiedLogGroupResponse.builder()
                .memberLogs(memberLogs)
                .adminLogs(adminLogs)
                .loginLogs(loginLogs)
                .build();
    }

    private UnifiedLogResponse toUnifiedDto(LogType logType, AdminActionLog log) {
        User user = userRepository.findById(log.getTargetId()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UnifiedLogResponse.builder()
                .logType(logType)
                .adminLoginId(log.getAdminLoginId())
                .actionType(log.getActionType())
                .page(log.getPage())
                .targetLoginId(user.getLoginId())
                .ipAddress(log.getIpAddress())
                .accessAt(log.getCreatedAt())
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
    public void validateLoginId(CustomUserDetails customUserDetails, String loginId) {
        globalService.validateAdmin(customUserDetails);

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

    public void updateLoginId(CustomUserDetails customUserDetails, String loginId, Admin admin) {
        globalService.validateAdmin(customUserDetails);

        // 1. 정규식: 영소문자 + 숫자 조합, 6~12자
        String regex = "^[a-z0-9]{6,12}$";
        if (!Pattern.matches(regex, loginId)) {
            throw new CustomException(ErrorCode.INVALID_LOGIN_ID_FORMAT);
        }
        // 2. DB 중복 확인
        if (userRepository.existsByLoginId(loginId)) {
            if(!admin.getLoginId().equals(loginId)){
                throw new CustomException(ErrorCode.DUPLICATED_LOGIN_ID);
            }
        }
    }
}
