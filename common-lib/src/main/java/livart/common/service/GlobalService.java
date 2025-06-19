package livart.common.service;

import livart.common.Auth.CustomUserDetails;
import livart.common.domain.alarm.entity.SmsTemplate;
import livart.common.domain.user.entity.User;
import livart.common.domain.user.repository.UserRepository;
import livart.common.dto.enums.ActionType;
import livart.common.dto.enums.alarm.*;
import livart.common.dto.enums.user.Role;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.log.entity.AdminActionLog;
import livart.common.log.entity.EmailLog;
import livart.common.log.entity.SmsLog;
import livart.common.log.repository.AdminActionLogRepository;
import livart.common.log.repository.EmailLogRepository;
import livart.common.log.repository.SmsLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.ses.endpoints.internal.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GlobalService {

    private final UserRepository userRepository;
    private final AdminActionLogRepository adminActionLogRepository;
    private final EmailLogRepository emailLogRepository;
    private final SmsLogRepository smsLogRepository;


    public User findUser(CustomUserDetails customUserDetails){
        User user = userRepository.findById(customUserDetails.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return user;
    }

    public User validateAdmin(CustomUserDetails userDetails) {

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() != Role.ADMIN && user.getRole() != Role.SUPER_ADMIN) {
            throw new CustomException(ErrorCode.ADMIN_ACCESS_DENIED);
        }

        return user;
    }

    @Transactional
    public void log(Long adminId,String adminLoginId, ActionType actionType, String page, String targetTable, Long targetId, String ip, String description) {
        adminActionLogRepository.save(AdminActionLog.builder()
                .adminId(adminId)
                .adminLoginId(adminLoginId)
                .actionType(actionType)
                .page(page)
                .targetTable(targetTable)
                .targetId(targetId)
                .ipAddress(ip)
                .description(description)
                .build());
    }

    @Transactional
    public void saveEmailLog(User user,AdType mailType, String senderEmail, String subject, String htmlBody, String senderName,
                             SendStatus status, String errorMessage, Long templateId, EmailForm emailForm,
                             String toEmail, Long senderAdmin) {

        EmailLog emailLog = EmailLog.builder()
                .emailForm(emailForm)
                .userId(user.getId())
                .mailType(mailType)
                .recipientEmail(toEmail)
                .senderEmail(senderEmail)
                .templateId(templateId)
                .senderAdmin(senderAdmin)
                .title(subject)
                .senderName(senderName)
                .content(htmlBody)
                .status(status)
                .sentAt(LocalDateTime.now())
                .errorMessage(errorMessage)
                .build();

        emailLogRepository.save(emailLog);
    }
    @Transactional
    public void saveAllEmailLog(List<User> users,AdType mailType, String sender, String subject, String htmlBody,String senderName,
                                SendStatus status, String errorMessage, Long templateId, EmailForm emailForm, Long senderAdmin) {
        LocalDateTime now = LocalDateTime.now();

        List<EmailLog> logs = users.stream()
                .map(user -> EmailLog.builder()
                        .emailForm(emailForm)
                        .userId(user.getId())
                        .mailType(mailType)
                        .recipientEmail(user.getEmail())
                        .senderEmail(sender)
                        .senderAdmin(senderAdmin)
                        .templateId(templateId)
                        .title(subject)
                        .senderName(senderName)
                        .content(htmlBody)
                        .status(status)
                        .sentAt(now)
                        .errorMessage(errorMessage)
                        .build())
                .toList();

        if (!logs.isEmpty()) {
            emailLogRepository.saveAll(logs);
        }
    }

    @Transactional
    public void logSmsSend(SmsForm smsForm, Long templateId, String senderNum, List<String> phoneNums,
                           String messageText, SmsSendReserveType reserveType, DefaultSmsType defaultSmsType,
                           SendStatus status, String errorMessage, LocalDateTime sentAt, LocalDateTime reservedAt) {
        Integer retryCount = reserveType == SmsSendReserveType.RESERVE ? 0 : null;

        List<SmsLog> log = phoneNums.stream()
                .map(p -> SmsLog.builder()
                        .smsForm(smsForm)
                        .templateId(templateId)
                        .senderNum(senderNum)
                        .phoneNum(p)
                        .content(messageText)
                        .sendReserveType(reserveType)
                        .defaultSmsType(defaultSmsType)
                        .status(status)
                        .sentAt(sentAt)
                        .reservedAt(reservedAt)
                        .errorMessage(errorMessage)
                        .retryCount(retryCount)
                        .build()
                ).collect(Collectors.toList());

        smsLogRepository.saveAll(log);
    }

}
