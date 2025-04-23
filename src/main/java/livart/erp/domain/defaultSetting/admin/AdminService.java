package livart.erp.domain.defaultSetting.admin;

import livart.common.Auth.CustomUserDetails;
import livart.common.domain.setting.entity.AllowedAdminIps;
import livart.common.domain.setting.repository.AllowedAdminIpsRepository;
import livart.common.domain.user.entity.Admin;
import livart.common.domain.user.entity.User;
import livart.common.domain.user.repository.AdminRepository;
import livart.common.domain.user.repository.UserRepository;
import livart.common.dto.enums.*;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.log.entity.AdminActionLog;
import livart.common.log.repository.AdminActionLogRepository;
import livart.common.log.repository.LoginHistoryRepository;
import livart.common.service.GlobalService;
import livart.common.mapper.SearchResult;
import livart.erp.domain.defaultSetting.admin.dto.request.AdminLogSearchRequest;
import livart.erp.domain.defaultSetting.admin.dto.request.AdminRequest;
import livart.erp.domain.defaultSetting.admin.dto.request.AdminSearchRequest;
import livart.erp.domain.defaultSetting.admin.dto.response.*;
import livart.erp.domain.defaultSetting.admin.enums.AdminSearchKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
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
    public AdminResponse createAdmin(CustomUserDetails customUserDetails,AdminRequest request){
        validateLoginId(customUserDetails, request.getLoginId());
        validatePassword(request.getPassword());

        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        Admin admin = adminRegisterMapper.toEntity(request).toBuilder()
                .password(encodedPassword)
                .role(Role.ADMIN)
                .provider(Provider.LOCAL)
                .status(UserStatus.ACTIVE)
                .adminRegister(true)
                .build();

        Admin saved = adminRepository.save(admin);

        if(request.getLimitIpAccess() == true){
            List<AllowedAdminIps> allowedAdminIpsList = request.getIpList()
                    .stream()
                    .map(ip -> AllowedAdminIps.builder()
                            .adminId(saved.getId())
                            .ipAddress(ip)
                            .build())
                    .collect(Collectors.toList());
            allowedAdminIpsRepository.saveAll(allowedAdminIpsList);
        }

        AdminResponse response = adminRegisterMapper.toDto(saved)
                .toBuilder()
                .adminId(saved.getId())
                .limitIpAccess(request.getLimitIpAccess())
                .ipList(request.getIpList())
                .build();

        return response;

    }

    public AdminResponse getAdmin(CustomUserDetails customUserDetails, Long adminId){
        User user = globalService.validateAdmin(customUserDetails);
        if(!user.getRole().equals(Role.SUPER_ADMIN)){
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<AllowedAdminIps> ips = allowedAdminIpsRepository.findByAdminId(adminId);

        List<String> ipList = ips.stream()
                .map(AllowedAdminIps::getIpAddress)
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

    public AdminResponse updateAdmin(CustomUserDetails customUserDetails, AdminRequest request, Long adminId){
        validateLoginId(customUserDetails, request.getLoginId());
        validatePassword(request.getPassword());

        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        adminRegisterMapper.updateEntityFromRequest(request, admin);
        admin.update(encodedPassword);
        Admin saved = adminRepository.save(admin);

        allowedAdminIpsRepository.deleteByAdminId(adminId);

        if(request.getLimitIpAccess()){
            List<AllowedAdminIps> allowedAdminIpsList = request.getIpList()
                    .stream()
                    .map(ip -> AllowedAdminIps.builder()
                            .adminId(saved.getId())
                            .ipAddress(ip)
                            .build())
                    .collect(Collectors.toList());
            allowedAdminIpsRepository.saveAll(allowedAdminIpsList);
        }

        AdminResponse response = adminRegisterMapper.toDto(saved)
                .toBuilder()
                .adminId(saved.getId())
                .limitIpAccess(request.getLimitIpAccess())
                .ipList(request.getIpList())
                .build();

        return response;
    }

    public SearchResult<AdminSearchResponse> getAdminList(
            CustomUserDetails customUserDetails, AdminSearchRequest request, Pageable pageable){

        globalService.validateAdmin(customUserDetails);

        String keyword = request.getKeyword();
        AdminSearchKey key = request.getKey();
        Boolean smsNotiEnabled = request.getSnsNotiEnabled();
        Long fullCount = adminRepository.count();
        Page<Admin> page;

        if (keyword == null || keyword.trim().isEmpty()) {
            // 키워드가 없는 경우 전체 목록 (필터 조건만 적용)
            page = adminRepository.findAllWithNotiFilter(smsNotiEnabled, pageable);
        } else {
            // 키워드가 있는 경우 필드별 분기
            page = switch (key) {
                case LOGIN_ID -> adminRepository.findByLoginId(keyword, smsNotiEnabled, pageable);
                case EMAIL -> adminRepository.findByEmail(keyword, smsNotiEnabled, pageable);
                case ADMIN_NAME -> adminRepository.findByAdminName(keyword, smsNotiEnabled, pageable);
                case PHONE_NUM -> adminRepository.findByPhoneNum(keyword, smsNotiEnabled, pageable);
                case OFFICE_NUM -> adminRepository.findByOfficeNum(keyword, smsNotiEnabled, pageable);
            };
        }

        List<AdminSearchResponse> results = page.getContent().stream()
                .map(admin -> {
                    AdminSearchResponse dto = adminSearchMapper.toDto(admin).toBuilder()
                            .adminId(admin.getId())
                            .registerdAt(LocalDate.from(admin.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDate()))
                            .lastLogin(admin.getLastLoginAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                            .build();
                    return dto;
                }).collect(Collectors.toList());

        return SearchResult.<AdminSearchResponse>builder()
                .fullCount(fullCount)
                .totalCount(page.getTotalElements())
                .page(page.getNumber())
                .size(page.getSize())
                .last(page.isLast())
                .data(results)
                .build();
    }

    public List<DelAdminResponse> deleteAdmins(CustomUserDetails customUserDetails, List<Long> adminIds){
        User user = globalService.validateAdmin(customUserDetails);
        if(!user.getRole().equals(Role.SUPER_ADMIN)){
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        List<DelAdminResponse> responseList = userRepository.findAllById(adminIds).stream()
                .map(users -> {
                    users.updateStatus(UserStatus.DELETED);
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

        Instant startInstant = startDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant(); // 2024-04-19T15:00:00Z
        Instant endInstant = endDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant();


        // 1. 회원 정보 열람 이력
        List<UnifiedLogResponse> memberLogs = adminActionLogRepository
                .findLogsByPartialLoginId(request.getAdminLoginId(),"user", startInstant, endInstant, pageable)
                .stream()
                .map(log -> toUnifiedDto(LogType.MEMBER, request.getAdminLoginId(), log))
                .collect(Collectors.toList());

        // 2. 운영자 정보 열람 이력
        List<UnifiedLogResponse> adminLogs = adminActionLogRepository
                .findLogsByPartialLoginId(request.getAdminLoginId(), "admin", startInstant, endInstant, pageable)
                .stream()
                .map(log -> toUnifiedDto(LogType.ADMIN, request.getAdminLoginId(), log))
                .collect(Collectors.toList());

        // 3. 로그인 기록
        List<UnifiedLogResponse> loginLogs = loginHistoryRepository
                .findByLoginIdLikeAndSuccessAndLoginAtBetween(request.getAdminLoginId(),true, startInstant, endInstant, pageable)
                .stream()
                .map(log -> UnifiedLogResponse.builder()
                        .logType(LogType.LOGIN)
                        .adminLoginId(request.getAdminLoginId())
                        .actionType(ActionType.LOGIN)
                        .page("관리자 ERP 로그인")
                        .targetLoginId(request.getAdminLoginId())
                        .ipAddress(log.getIpAddress())
                        .accessAt(log.getAttemptedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                        .build())
                .collect(Collectors.toList());

        // 통합
        return UnifiedLogGroupResponse.builder()
                .memberLogs(memberLogs)
                .adminLogs(adminLogs)
                .loginLogs(loginLogs)
                .build();
    }

    private UnifiedLogResponse toUnifiedDto(LogType logType, String adminLoginId, AdminActionLog log) {
        User user = userRepository.findById(log.getTargetId()).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UnifiedLogResponse.builder()
                .logType(logType)
                .adminLoginId(adminLoginId)
                .actionType(log.getActionType())
                .page(log.getPage())
                .targetLoginId(user.getLoginId())
                .ipAddress(log.getIpAddress())
                .accessAt(log.getCreatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
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
        User user = globalService.validateAdmin(customUserDetails);
        if(!user.getRole().equals(Role.SUPER_ADMIN)){
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
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
