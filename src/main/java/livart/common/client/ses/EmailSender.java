package livart.common.client.ses;

import livart.common.domain.alarm.entity.EmailSetting;
import livart.common.domain.alarm.repository.EmailSettingRepository;
import livart.common.domain.user.entity.User;
import livart.common.domain.user.repository.UserRepository;
import livart.common.dto.enums.alarm.AdType;
import livart.common.dto.enums.alarm.EmailForm;
import livart.common.dto.enums.alarm.SendStatus;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.service.GlobalService;
import livart.erp.domain.alarm.dto.response.SendResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailSender {
    private final UserRepository userRepository;
    private final EmailSettingRepository emailSettingRepository;
    private final SesClient sesClient;
    private final GlobalService globalService;
    @Transactional
    public void sendEmailToOne(String subject,AdType mailType, String htmlBody, Long userId, Long templateId, EmailForm emailForm, Long senderAdmin) {
        EmailSetting setting = emailSettingRepository.findFirstByIsActiveTrue()
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_SETTING_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String toEmail = user.getEmail() != null || StringUtils.hasText(user.getEmail()) ? user.getEmail().trim() : null;

        String fromEmail = setting.getFromEmail() != null ? setting.getFromEmail().trim() : "";
        String replyEmail = setting.getReplyEmail() != null ? setting.getReplyEmail().trim() : "";
        String senderName = setting.getSenderName() != null
                ? setting.getSenderName()
                .replaceAll("[\\r\\n\\t\\x00-\\x1F\\x7F\\u200B\\u00A0]", "") // 특수 공백 제거
                .trim()
                : "";

        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .source(String.format("%s <%s>", senderName , fromEmail))
                .destination(Destination.builder()
                        .toAddresses(toEmail)
                        .build())
                .replyToAddresses(setting.getReplyEmail())
                .message(Message.builder()
                        .subject(Content.builder().data(subject).charset("UTF-8").build())
                        .body(Body.builder()
                                .html(Content.builder().data(htmlBody).charset("UTF-8").build())
                                .build())
                        .build())
                .build();

        try {
            sesClient.sendEmail(emailRequest);
            globalService.saveEmailLog(user,mailType, fromEmail, subject, htmlBody, senderName, SendStatus.SENT,null, templateId, emailForm, toEmail, senderAdmin);
        } catch (SesException e) {
            log.error("이메일 발송 실패: {}", e.awsErrorDetails().errorMessage());
            globalService.saveEmailLog(user,mailType, fromEmail, subject, htmlBody, senderName, SendStatus.FAILED, e.awsErrorDetails().errorMessage(), templateId, emailForm, toEmail, senderAdmin);
        }
    }

    @Transactional
    public SendResult sendEmailToMany(EmailSetting setting, AdType mailType, String subject, String htmlBody, List<Long> idList, Long templateId, EmailForm emailForm, Long senderAdmin) {
        final int BATCH_SIZE = 50; // SES 1회 최대 수신자 수

        Long successCount = 0L;
        Long failCount = 0L;

        List<User> user = userRepository.findAllById(idList);

        for (int i = 0; i < user.size(); i += BATCH_SIZE) {
            List<User> batchUsers = user.subList(i, Math.min(i + BATCH_SIZE, user.size()));
            String fromEmail = setting.getFromEmail() != null ? setting.getFromEmail().trim() : "";
            String replyEmail = setting.getReplyEmail() != null ? setting.getReplyEmail().trim() : "";
            String senderName = setting.getSenderName() != null
                    ? setting.getSenderName()
                    .replaceAll("[\\r\\n\\t\\x00-\\x1F\\x7F\\u200B\\u00A0]", "") // 특수 공백 제거
                    .trim()
                    : "";

            List<String> batchEmails = batchUsers.stream()
                    .map(User::getEmail)
                    .filter(email -> StringUtils.hasText(email)) // null 또는 빈 문자열 제거
                    .map(String::trim) // 공백 제거
                    .toList();

            SendEmailRequest emailRequest = SendEmailRequest.builder()
                    .source(String.format("%s <%s>", senderName, fromEmail))
                    .destination(Destination.builder()
                            .toAddresses(batchEmails)
                            .build())
                    .replyToAddresses(replyEmail)
                    .message(Message.builder()
                            .subject(Content.builder().data(subject).charset("UTF-8").build())
                            .body(Body.builder()
                                    .html(Content.builder().data(htmlBody).charset("UTF-8").build())
                                    .build())
                            .build())
                    .build();

            try {
                sesClient.sendEmail(emailRequest);
                globalService.saveAllEmailLog(batchUsers,mailType, fromEmail, subject, htmlBody, senderName, SendStatus.SENT,null, templateId, emailForm, senderAdmin);
                successCount += batchUsers.size();
            } catch (SesException e) {
                globalService.saveAllEmailLog(batchUsers,mailType, fromEmail, subject, htmlBody, senderName, SendStatus.FAILED, e.awsErrorDetails().errorMessage(), templateId, emailForm, senderAdmin);
                failCount += batchUsers.size();
            }
        }

        return SendResult.builder()
                .recipientCount(idList.stream().count())
                .successCount(successCount)
                .failCount(failCount)
                .build();
    }
}
