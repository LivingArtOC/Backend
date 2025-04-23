package livart.common.service;

import livart.common.Auth.CustomUserDetails;
import livart.common.domain.user.entity.Admin;
import livart.common.domain.user.entity.User;
import livart.common.domain.user.repository.UserRepository;
import livart.common.dto.enums.ActionType;
import livart.common.dto.enums.Role;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.log.entity.AdminActionLog;
import livart.common.log.repository.AdminActionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GlobalService {

    private final UserRepository userRepository;
    private final AdminActionLogRepository adminActionLogRepository;

    public User findUser(CustomUserDetails customUserDetails){
        User user = userRepository.findById(customUserDetails.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return user;
    }

    public User validateAdmin(CustomUserDetails userDetails) {

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() != Role.ADMIN) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }

        return user;
    }

    public void log(Long adminId,String adminLoginId, ActionType actionType, String page, String targetTable, Long targetId, String ip) {
        adminActionLogRepository.save(AdminActionLog.builder()
                .adminId(adminId)
                .adminLoginId(adminLoginId)
                .actionType(actionType)
                .page(page)
                .targetTable(targetTable)
                .targetId(targetId)
                .ipAddress(ip)
                .build());
    }

    public void logAll(Long adminId,String adminLoginId, ActionType actionType, String page, List<TargetInfo> targets, String ip) {
        for (TargetInfo target : targets) {
            log(adminId,adminLoginId, actionType, page, target.table(), target.targetId, ip);
        }
    }

    public record TargetInfo(String table, Long targetId) {}
}
