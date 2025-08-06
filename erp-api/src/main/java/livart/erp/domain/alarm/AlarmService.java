package livart.erp.domain.alarm;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import livart.common.Auth.CustomUserDetails;
import livart.common.client.ses.EmailSender;
import livart.common.client.sms.SmsSender;
import livart.common.domain.alarm.entity.*;
import livart.common.domain.alarm.repository.*;
import livart.common.domain.order.entity.QOrder;
import livart.common.domain.user.entity.QBusiness;
import livart.common.domain.user.entity.QConsumer;
import livart.common.domain.user.entity.QUser;
import livart.common.domain.user.entity.User;
import livart.common.dto.enums.alarm.*;
import livart.common.dto.enums.user.Role;
import livart.common.dto.enums.user.UserStatus;
import livart.common.dto.request.SmsAutoDto;
import livart.common.dto.response.SendResult;
import livart.common.dto.response.SmsSendResult;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.log.entity.*;
import livart.common.log.repository.EmailLogRepository;
import livart.common.log.repository.SmsLogRepository;
import livart.common.mapper.SearchResult;
import livart.common.service.GlobalService;
import livart.erp.domain.alarm.dto.request.*;
import livart.erp.domain.alarm.dto.response.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmService {
    private final GlobalService globalService;
    private final SmsSender smsSender;
    private final EmailSender emailSender;
    private final SmsSettingRepository smsSettingRepository;
    private final EmailSettingRepository emailSettingRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final MailTemplateRepository mailTemplateRepository;
    private final EmailLogRepository emailLogRepository;
    private final SmsTemplateRepository smsTemplateRepository;
    private final SmsContentRepository smsContentRepository;
    private final SmsLogRepository smsLogRepository;

    @Scheduled(cron = "0 0 * * * *")
    public void scheduledPointRefresh() {
        try {
            smsSender.updateRemainingPoint();
        } catch (Exception e) {
            log.error("CoolSMS 포인트 자동 갱신 실패", e);
        }
    }

    @Transactional
    public AlarmPointResponse getPoint(CustomUserDetails customUserDetails){
        try {
            globalService.validateAdmin(customUserDetails);
            SmsSetting setting = smsSender.updateRemainingPoint();
            return AlarmPointResponse.builder()
                    .point(setting.getSmsPoint())
                    .pointUpdateTime(setting.getUpdatedAt())
                    .build();
        } catch (Exception e){
            log.error("CoolSMS 포인트 갱신 및 조회 실패", e);
            throw new CustomException(ErrorCode.SMS_BALANCE_FETCH_FAILED);
        }
    }

    @Transactional
    public SmsKakaoSettingDto updateSetting(CustomUserDetails customUserDetails, SmsKakaoSettingDto request){
        globalService.validateAdmin(customUserDetails);

        if (!StringUtils.hasText(request.getSmsApiKey()) || !StringUtils.hasText(request.getSmsApiSecret()) || !StringUtils.hasText(request.getSenderNum())) {
            throw new CustomException(ErrorCode.INVALID_SMS_SETTING);
        }

        SmsSetting setting = smsSettingRepository.findFirstByIsActiveTrue()
                .map(s -> {

                    BigDecimal point = smsSender.remainingPoint(request.getSmsApiKey(), request.getSmsApiSecret());
                    s.updatePoint(point);

                    s.update(request.getSmsApiKey(), request.getSmsApiSecret(), request.getSenderNum(), request.getRejectNum(), request.getRejectUrl(), request.getServiceNum(), customUserDetails.getId());
                    return s;
                })
                .orElseGet(() -> {
                    BigDecimal point = smsSender.remainingPoint(request.getSmsApiKey(), request.getSmsApiSecret());

                    return SmsSetting.builder()
                            .smsPoint(point)
                            .smsApiKey(request.getSmsApiKey())
                            .smsApiSecret(request.getSmsApiSecret())
                            .senderNum(request.getSenderNum())
                            .rejectUrl(request.getRejectUrl())
                            .rejectNum(request.getRejectNum())
                            .serviceNum(request.getServiceNum())
                            .isActive(true)
                            .updatedBy(customUserDetails.getId())
                            .defaultSmsType(DefaultSmsType.ONLY_90_BYTES)
                            .build();
                });

        SmsSetting saved = smsSettingRepository.save(setting);

        return SmsKakaoSettingDto.builder()
                .smsApiKey(saved.getSmsApiKey())
                .smsApiSecret(saved.getSmsApiSecret())
                .senderNum(saved.getSenderNum())
                .rejectUrl(saved.getRejectUrl())
                .rejectNum(saved.getRejectNum())
                .serviceNum(saved.getServiceNum())
                .build();
    }

    public SmsKakaoSettingDto getSetting(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        SmsSetting setting = smsSender.updateRemainingPoint();

        String secret = setting.getSmsApiSecret();
        String maskedSecret = secret.length() > 4
                ? secret.substring(0, 4) + "*".repeat(secret.length() - 4)
                : "*".repeat(secret.length());

        return SmsKakaoSettingDto.builder()
                .smsApiKey(setting.getSmsApiKey())
                .smsApiSecret(maskedSecret)
                .senderNum(setting.getSenderNum())
                .rejectUrl(setting.getRejectUrl())
                .rejectNum(setting.getRejectNum())
                .serviceNum(setting.getServiceNum())
                .build();
    }

    @Transactional
    public SmsSettingResponse updateSmsSetting(CustomUserDetails customUserDetails, SmsUpdateRequest request){
        globalService.validateAdmin(customUserDetails);

        if(request.getDefaultSmsType() == null){
            throw new CustomException(ErrorCode.INVALID_TYPE);
        }

        SmsSetting setting = smsSender.updateRemainingPoint();
        setting.updateType(request.getDefaultSmsType(), customUserDetails.getId());
        SmsSetting saved = smsSettingRepository.save(setting);

        return SmsSettingResponse.builder()
                .smsPoint(saved.getSmsPoint())
                .senderNum(saved.getSenderNum())
                .rejectUrl(saved.getRejectUrl())
                .rejectNum(saved.getRejectNum())
                .defaultSmsType(saved.getDefaultSmsType())
                .build();
    }

    public SmsSettingResponse getSmsSetting(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        SmsSetting setting = smsSender.updateRemainingPoint();

        return SmsSettingResponse.builder()
                .smsPoint(setting.getSmsPoint())
                .senderNum(setting.getSenderNum())
                .rejectUrl(setting.getRejectUrl())
                .rejectNum(setting.getRejectNum())
                .defaultSmsType(setting.getDefaultSmsType())
                .build();
    }

    @Transactional
    public EmailSettingDto updateEmailSetting(CustomUserDetails customUserDetails, EmailSettingDto request){
        globalService.validateAdmin(customUserDetails);

        if (!StringUtils.hasText(request.getFromEmail()) || !StringUtils.hasText(request.getToEmail()) || !StringUtils.hasText(request.getReplyEmail())) {
            throw new CustomException(ErrorCode.INVALID_EMAIL_SETTING);
        }

        EmailSetting setting = emailSettingRepository.findFirstByIsActiveTrue()
                .map(s -> {
                    s.update(request.getSenderName(), request.getFromEmail(), request.getToEmail(), request.getReplyEmail(), request.getIsAgreed(), customUserDetails.getId());
                    return s;
                })
                .orElseGet(() -> EmailSetting.builder()
                        .senderName(request.getSenderName())
                        .fromEmail(request.getFromEmail())
                        .toEmail(request.getToEmail())
                        .replyEmail(request.getReplyEmail())
                        .isAgreed(request.getIsAgreed())
                        .isActive(true)
                        .updatedBy(customUserDetails.getId())
                        .build());

        EmailSetting saved = emailSettingRepository.save(setting);

        return EmailSettingDto.builder()
                .senderName(saved.getSenderName())
                .fromEmail(saved.getFromEmail())
                .toEmail(saved.getToEmail())
                .replyEmail(saved.getReplyEmail())
                .isAgreed(saved.getIsAgreed())
                .build();
    }

    public EmailSettingDto getEmailSetting(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        EmailSetting setting = emailSettingRepository.findFirstByIsActiveTrue()
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_SETTING_NOT_FOUND));

        return EmailSettingDto.builder()
                .senderName(setting.getSenderName())
                .fromEmail(setting.getFromEmail())
                .toEmail(setting.getToEmail())
                .replyEmail(setting.getReplyEmail())
                .isAgreed(setting.getIsAgreed())
                .build();
    }

    public SearchResult<MemberSearchResponse> searchMember(CustomUserDetails customUserDetails, MemberAddRequest request, Pageable pageable) {
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
                case ALL -> {
                    BooleanBuilder allBuilder = new BooleanBuilder();
                    allBuilder.or(consumer.name.containsIgnoreCase(request.getKeyword()));
                    allBuilder.or(user.loginId.containsIgnoreCase(request.getKeyword()));
                    allBuilder.or(user.phoneNum.containsIgnoreCase(request.getKeyword()));
                    allBuilder.or(user.email.containsIgnoreCase(request.getKeyword()));
                    allBuilder.or(business.bizName.containsIgnoreCase(request.getKeyword()));
                    allBuilder.or(business.bizRegistrationNum.containsIgnoreCase(request.getKeyword()));
                    builder.and(allBuilder);
                }
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

        if(request.getProvider() != null){
            builder.and(user.provider.eq(request.getProvider()));
        }

        Integer min = request.getOrderCount() != null ? request.getOrderCount().getStart() : null;
        Integer max = request.getOrderCount() != null ? request.getOrderCount().getEnd() : null;

        JPQLQuery<MemberSearchResponse> query = jpaQueryFactory
                .select(Projections.constructor(MemberSearchResponse.class,
                        user.id,
                        user.status,
                        user.loginId,
                        user.userName,
                        user.role,
                        user.email,
                        user.phoneNum,
                        userMKConsent.emailNotice,
                        userMKConsent.smsNotice
                        )
                )
                .from(user)
                .leftJoin(userMKConsent).on(userMKConsent.user.id.eq(user.id))
                .leftJoin(orders).on(orders.userId.eq(user.id))
                .where(builder)
                .groupBy(user.id, user.userName, user.email, user.phoneNum,
                        userMKConsent.emailNotice, userMKConsent.smsNotice,
                        user.loginId, user.role, user.status)
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

        if (min != null && max != null) {
            countQuery.having(orders.count().between(min.longValue(), max.longValue()));
        } else if (min != null) {
            countQuery.having(orders.count().goe(min.longValue()));
        } else if (max != null) {
            countQuery.having(orders.count().loe(max.longValue()));
        }

        long totalCount = countQuery.fetch().size();

        return SearchResult.<MemberSearchResponse> builder()
                .totalCount(totalCount)
                .size(pageable.getPageSize())
                .page(pageable.getPageNumber())
                .data(responses)
                .build();
    }

    public EmailInfoResponse getEmailInfo(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        EmailSetting setting = emailSettingRepository.findFirstByIsActiveTrue()
                .orElse(null);

        QUser user = QUser.user;
        QUserMKConsent userMKConsent = QUserMKConsent.userMKConsent;

        Long totalUsers = jpaQueryFactory
                .select(user.count())
                .from(user)
                .fetchOne();

        Long mkUserCount = jpaQueryFactory
                .select(user.count())
                .from(user)
                .join(user.userMarketingNotices, userMKConsent)
                .fetchOne();

        Long emailOptOut = jpaQueryFactory
                .select(userMKConsent.count())
                .from(userMKConsent)
                .where(userMKConsent.emailNotice.isFalse())
                .fetchOne();

        String fromEmail = null;
        String rejectUrl = null;
        Boolean isRejected = null;
        String lastRejectMessage = null;
        String lastSendMessage = null;

        if (setting != null) {
            fromEmail = setting.getFromEmail();
            rejectUrl = setting.getRejectUrl();
            isRejected = setting.getIsRejected();
            lastRejectMessage = setting.getLastRejectMessage();
            lastSendMessage = setting.getLastSendMessage();
        }

        return EmailInfoResponse.builder()
                .fromEMail(fromEmail)
                .allUserCount(totalUsers)
                .mkUserCount(mkUserCount)
                .rejectUserCount(emailOptOut)
                .rejectUrl(rejectUrl)
                .isRejected(isRejected)
                .lastRejectMessage(lastRejectMessage)
                .lastSendMessage(lastSendMessage)
                .build();
    }

    @Transactional
    public SendResult sendEmail(CustomUserDetails customUserDetails, EmailSendRequest request){
        globalService.validateAdmin(customUserDetails);

        EmailSetting setting = emailSettingRepository.findFirstByIsActiveTrue()
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_SETTING_NOT_FOUND));

        if(request.getTitle() == null || request.getContent() == null){
            throw new CustomException(ErrorCode.NULL_INPUT_VALUE);
        }

        QUser user = QUser.user;
        QUserMKConsent userMKConsent = QUserMKConsent.userMKConsent;
        BooleanBuilder builder = new BooleanBuilder();

        if (Boolean.TRUE.equals(request.getOnlyAllowedUser())) {
            builder.and(userMKConsent.emailNotice.eq(true));
        }

        if (request.getIdList() == null || request.getIdList().isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_SEND_TARGET);
        }

        builder.and(user.id.in(request.getIdList()));

        List<Long> userList = jpaQueryFactory
                .select(user.id)
                .from(user)
                .join(user.userMarketingNotices, userMKConsent)
                .where(builder)
                .fetch();

        if (userList.size() != request.getIdList().size()) {
            throw new CustomException(ErrorCode.INVALID_SEND_TARGET);
        }

        StringBuilder bodyBuilder = new StringBuilder(request.getContent());

        if (Boolean.TRUE.equals(request.getIsRejected()) && StringUtils.hasText(request.getRejectMessage())) {
            bodyBuilder.append("<br><br>");
            bodyBuilder.append("<hr style='border:0; border-top:1px solid #ccc; margin:20px 0;'>");
            bodyBuilder.append("<div style='font-size:12px; color:#999; line-height:1.6;'>");
            bodyBuilder.append(request.getRejectMessage());
            bodyBuilder.append("</div>");
        }

        String htmlBody = bodyBuilder.toString();

        setting.updateMessage(request.getRejectMessage(), request.getContent(), request.getIsRejected(), customUserDetails.getId());

        return emailSender.sendEmailToMany(setting,request.getMailType(), request.getTitle(), htmlBody, userList, null, EmailForm.GENERAL, customUserDetails.getId());
    }

    public EmailAutoResponse getAutoEmailSetting(CustomUserDetails customUserDetails, String type){
        globalService.validateAdmin(customUserDetails);
        EmailType emailType = validateType(type);

        MailTemplate template = mailTemplateRepository.findByType(emailType)
                .orElse(null);

        String fromEmail = emailSettingRepository.findFirstByIsActiveTrue()
                .map(EmailSetting::getFromEmail)
                .orElse("메일 기본 설정을 먼저 진행해주세요.");

        if(template != null){
            return EmailAutoResponse.builder()
                    .emailAutoType(template.getEmailAutoType())
                    .type(template.getType())
                    .isAutoSend(template.getIsAutoSend())
                    .sendStandardDate(template.getSendStandardDate())
                    .sendMethod(template.getSendMethod())
                    .title(template.getTitle())
                    .content(template.getContent())
                    .sendEmail(fromEmail)
                    .build();
        }else {
            return EmailAutoResponse.builder()
                    .sendEmail(fromEmail)
                    .build();
        }
    }
    
    @Transactional
    public EmailAutoResponse updateAutoEmailSetting(CustomUserDetails customUserDetails, EmailAutoRequest request, String type){
        globalService.validateAdmin(customUserDetails);
        EmailType emailType = validateType(type);
        
        MailTemplate template = mailTemplateRepository.findByType(emailType)
                .map(m -> {
                    m.update(request.getEmailAutoType(), request.getIsAutoSend(), request.getTitle(), request.getSendStandardDate(), request.getSendMethod(), request.getContent(), customUserDetails.getId());
                    return m;
                })
                .orElseGet(
                        () -> MailTemplate.builder()
                                .emailAutoType(request.getEmailAutoType())
                                .type(emailType)
                                .isAutoSend(request.getIsAutoSend())
                                .title(request.getTitle())
                                .sendStandardDate(request.getSendStandardDate())
                                .sendMethod(request.getSendMethod())
                                .content(request.getContent())
                                .updatedBy(customUserDetails.getId())
                                .build()
                );
        
        MailTemplate saved = mailTemplateRepository.save(template);

        String fromEmail = emailSettingRepository.findFirstByIsActiveTrue()
                .map(EmailSetting::getFromEmail)
                .orElse("메일 기본 설정을 먼저 진행해주세요.");
        
        return EmailAutoResponse.builder()
                    .emailAutoType(saved.getEmailAutoType())
                    .type(saved.getType())
                    .isAutoSend(saved.getIsAutoSend())
                    .sendStandardDate(saved.getSendStandardDate())
                    .sendMethod(saved.getSendMethod())
                    .title(saved.getTitle())
                    .content(saved.getContent())
                    .sendEmail(fromEmail)
                    .build();
    }

    private EmailType validateType(String type){
        if(EmailType.contains(type)) {
            return EmailType.valueOf(type.toUpperCase());
        }else {
            throw new CustomException(ErrorCode.INVALID_TYPE);
        }
    }

    public SearchResult<EmailSearchResponse> searchEmailLog(CustomUserDetails customUserDetails, EmailSearchRequest request, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        QEmailLog emailLog = QEmailLog.emailLog;
        BooleanBuilder builder = new BooleanBuilder()
                .and(emailLog.status.eq(SendStatus.SENT));

        if(StringUtils.hasText(request.getKeyword()) && request.getKey() != null){
            switch (request.getKey()){
                case SENDER -> builder.and(emailLog.senderEmail.containsIgnoreCase(request.getKeyword()));
                case RECIPIENT -> builder.and(emailLog.recipientEmail.containsIgnoreCase(request.getKeyword()));
                case TITLE -> builder.and(emailLog.title.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keywordBuilder = new BooleanBuilder();
                    keywordBuilder.or(emailLog.senderEmail.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(emailLog.recipientEmail.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(emailLog.title.containsIgnoreCase(request.getKeyword()));
                    builder.and(keywordBuilder);
                }
            }
        }

        if(request.getEmailForm() != null && request.getEmailForm() != EmailForm.ALL){
            builder.and(emailLog.emailForm.eq(request.getEmailForm()));
        }

        if(request.getSentDate() != null){
            if(request.getSentDate().getStartDate() != null){
                builder.and(emailLog.sentAt.goe(request.getSentDate().getStartDate().atStartOfDay()));
            }

            if(request.getSentDate().getEndDate() != null){
                builder.and(emailLog.sentAt.loe(request.getSentDate().getEndDate().atTime(23,59,59)));
            }
        }

        List<EmailLog> logs = jpaQueryFactory
                .selectFrom(emailLog)
                .where(builder)
                .orderBy(emailLog.sentAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<EmailSearchResponse> responses = logs.stream()
                .map(log -> EmailSearchResponse.builder()
                        .logId(log.getId())
                        .emailForm(log.getEmailForm())
                        .title(log.getTitle())
                        .senderName(log.getSenderName())
                        .sendDate(log.getSentAt().toLocalDate())
                        .recipientEmail(log.getRecipientEmail())
                        .build()
                ).collect(Collectors.toList());

        Long totalCount = jpaQueryFactory
                .select(emailLog.count())
                .from(emailLog)
                .where(builder)
                .fetchOne();

        return SearchResult.<EmailSearchResponse> builder()
                .totalCount(totalCount != null ? totalCount : 0L)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();
    }

    public EmailContentResponse getEmailContent(CustomUserDetails customUserDetails, Long logId){
        globalService.validateAdmin(customUserDetails);

        EmailLog emailLog = emailLogRepository.findById(logId)
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_LOG_NOT_FOUND));

        return EmailContentResponse.builder()
                .logId(emailLog.getId())
                .title(emailLog.getTitle())
                .content(emailLog.getContent())
                .build();
    }

    @Transactional
    public List<SmsAutoDto> updateAutoSmsSetting(CustomUserDetails customUserDetails, List<SmsAutoDto> requests){
        globalService.validateAdmin(customUserDetails);

        List<SmsType> typeList = requests.stream()
                .map(SmsAutoDto::getType)
                .collect(Collectors.toList());

        Map<SmsType, SmsTemplate> templateMap = smsTemplateRepository.findAllByTypeIn(typeList).stream()
                .collect(Collectors.toMap(SmsTemplate::getType, Function.identity()));

        List<SmsTemplate> templates = requests.stream()
                .map(r -> {
                    SmsTemplate template = templateMap.get(r.getType());

                    if(template == null){
                        return SmsTemplate.builder()
                                .smsAutoType(r.getSmsAutoType())
                                .type(r.getType())
                                .sendStandardDate(r.getSendStandardDate())
                                .resendDate(r.getResendDate())
                                .resendTime(r.getResendTime())
                                .overNightSend(r.getOverNightSend())
                                .smsSendClass(r.getSmsSendClass())
                                .isAutoSendAdmin(r.getIsAutoSendAdmin())
                                .isAutoSendMember(r.getIsAutoSendMember())
                                .adminContent(r.getAdminContent())
                                .memberContent(r.getMemberContent())
                                .updatedBy(customUserDetails.getId())
                                .build();
                    } else {
                        template.update(r, customUserDetails.getId());
                        return template;
                    }
                })
                .collect(Collectors.toList());

        List<SmsTemplate> saved = smsTemplateRepository.saveAll(templates);

        return toDto(saved);
    }

    public List<SmsAutoDto> getAutoSmsSetting(CustomUserDetails customUserDetails, String smsAutoType){
        globalService.validateAdmin(customUserDetails);

        SmsAutoType autoType = validateSmsAutoType(smsAutoType);

        List<SmsTemplate> smsTemplates = smsTemplateRepository.findAllBySmsAutoType(autoType);

        if(smsTemplates.isEmpty()){
            throw new CustomException(ErrorCode.SMS_TEMPLATE_NOT_FOUND);
        }

        return toDto(smsTemplates);
    }

    private SmsAutoType validateSmsAutoType(String type){
        if(SmsAutoType.contains(type)) {
            return SmsAutoType.valueOf(type.toUpperCase());
        }else {
            throw new CustomException(ErrorCode.INVALID_TYPE);
        }
    }

    private List<SmsAutoDto> toDto(List<SmsTemplate> templates){
        return templates.stream()
                .map(r -> SmsAutoDto.builder()
                        .smsAutoType(r.getSmsAutoType())
                        .type(r.getType())
                        .sendStandardDate(r.getSendStandardDate())
                        .resendDate(r.getResendDate())
                        .resendTime(r.getResendTime())
                        .overNightSend(r.getOverNightSend())
                        .smsSendClass(r.getSmsSendClass())
                        .isAutoSendAdmin(r.getIsAutoSendAdmin())
                        .isAutoSendMember(r.getIsAutoSendMember())
                        .adminContent(r.getAdminContent())
                        .memberContent(r.getMemberContent())
                        .build()
                ).collect(Collectors.toList());
    }

    public SmsInfoResponse getSmsInfo(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        SmsSetting setting = smsSender.updateRemainingPoint();

        return SmsInfoResponse.builder()
                .smsPoint(setting.getSmsPoint())
                .senderNum(setting.getSenderNum())
                .rejectNum(setting.getRejectNum())
                .commercialMessage(setting.getCommercialMessage())
                .build();
    }

    @Transactional
    public SmsSendResult sendSms(CustomUserDetails customUserDetails, SmsSendRequest request){
        globalService.validateAdmin(customUserDetails);

        SmsSetting setting = smsSender.updateRemainingPoint();

        QUser user = QUser.user;
        QUserMKConsent userMKConsent = QUserMKConsent.userMKConsent;
        BooleanBuilder builder = new BooleanBuilder();

        if (Boolean.TRUE.equals(request.getOnlyAllowedUser())) {
            builder.and(userMKConsent.smsNotice.eq(true));
        }

        builder.and(user.id.in(request.getUserList()));

        List<User> userList = jpaQueryFactory
                .selectFrom(user)
                .join(user.userMarketingNotices, userMKConsent)
                .where(builder)
                .fetch();

        List<String> phoneNums = userList.stream()
                .map(User::getPhoneNum)
                .collect(Collectors.toList());

        if (userList.size() != request.getUserList().size()) {
            throw new CustomException(ErrorCode.INVALID_SEND_TARGET);
        }

        return smsSender.sendSmsAll(phoneNums, SmsForm.GENERAL, null, request.getSendReserveType(), setting.getDefaultSmsType(), request.getContent(), request.getReserveDateTime());
    }

    @Transactional
    public ContentResponse saveContent(CustomUserDetails customUserDetails, ContentRequest request){
        globalService.validateAdmin(customUserDetails);

        SmsContent content = SmsContent.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .updatedBy(customUserDetails.getId())
                .build();

        SmsContent saved = smsContentRepository.save(content);

        return ContentResponse.builder()
                .contentId(saved.getId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .build();
    }

    public List<ContentResponse> getContent(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        return smsContentRepository.findAll().stream()
                .map(c -> ContentResponse.builder()
                        .contentId(c.getId())
                        .title(c.getTitle())
                        .content(c.getContent())
                        .build()
                ).collect(Collectors.toList());
    }

    @Transactional
    public ContentResponse updateContent(CustomUserDetails customUserDetails, ContentRequest request, Long contentId){
        globalService.validateAdmin(customUserDetails);

        SmsContent content = smsContentRepository.findById(contentId)
                .orElseThrow(() -> new CustomException(ErrorCode.SMS_CONTENT_NOT_FOUND));

        content.update(request.getTitle(), request.getContent(), customUserDetails.getId());

        SmsContent saved = smsContentRepository.save(content);

        return ContentResponse.builder()
                .contentId(saved.getId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .build();
    }
    @Transactional
    public void deleteContent(CustomUserDetails customUserDetails, Long contentId){
        globalService.validateAdmin(customUserDetails);

        smsContentRepository.deleteById(contentId);

    }
    public SearchResult<SmsSearchResponse> searchSmsLog(CustomUserDetails customUserDetails, SmsSearchRequest request, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        QSmsLog smsLog = QSmsLog.smsLog;
        BooleanBuilder builder = new BooleanBuilder();

        if(StringUtils.hasText(request.getKeyword()) && request.getKey() != null){
            switch (request.getKey()){
                case SENDER -> builder.and(smsLog.senderNum.containsIgnoreCase(request.getKeyword()));
                case RECIPIENT -> builder.and(smsLog.phoneNum.containsIgnoreCase(request.getKeyword()));
                case CONTENT -> builder.and(smsLog.content.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder allBuilder = new BooleanBuilder();
                    allBuilder.or(smsLog.content.containsIgnoreCase(request.getKeyword()));
                    allBuilder.or(smsLog.phoneNum.containsIgnoreCase(request.getKeyword()));
                    allBuilder.or(smsLog.senderNum.containsIgnoreCase(request.getKeyword()));
                    builder.and(allBuilder);
                }
            }
        }

        if(request.getSmsForm() != null && request.getSmsForm() != SmsForm.ALL){
            builder.and(smsLog.smsForm.eq(request.getSmsForm()));
        }

        if(request.getReserveType() != null && request.getReserveType() != SmsSendReserveType.ALL){
            builder.and(smsLog.sendReserveType.eq(request.getReserveType()));
        }

        if (request.getSendAt() != null) {
            LocalDateTime start = request.getSendAt().getStartDate() != null ? request.getSendAt().getStartDate().atStartOfDay() : null;
            LocalDateTime end = request.getSendAt().getEndDate() != null ? request.getSendAt().getEndDate().atTime(23, 59, 59) : null;

            BooleanBuilder dateCondition = new BooleanBuilder();

            if (start != null) {
                dateCondition.or(smsLog.sentAt.goe(start));
                dateCondition.or(smsLog.reservedAt.goe(start));
            }
            if (end != null) {
                dateCondition.or(smsLog.sentAt.loe(end));
                dateCondition.or(smsLog.reservedAt.loe(end));
            }

            builder.and(dateCondition);
        }

        if(request.getSmsDivision() != null){
            if(request.getSmsDivision() == SmsDivision.LMS){
                builder.and(smsLog.defaultSmsType.eq(DefaultSmsType.LMS));
            }
            if(request.getSmsDivision() == SmsDivision.SMS){
                BooleanBuilder div = new BooleanBuilder();
                div.or(smsLog.defaultSmsType.eq(DefaultSmsType.DIVISION_SMS));
                div.or(smsLog.defaultSmsType.eq(DefaultSmsType.ONLY_90_BYTES));
                builder.and(div);
            }
        }

        if(request.getStatus() != null && request.getStatus() != SendStatus.ALL){
            builder.and(smsLog.status.eq(request.getStatus()));
        }

        List<SmsLog> logs = jpaQueryFactory
                .selectFrom(smsLog)
                .where(builder)
                .orderBy(
                        new CaseBuilder()
                                .when(smsLog.sentAt.isNotNull()).then(1)
                                .otherwise(0).desc(),
                        smsLog.sentAt.desc().nullsLast(),
                        smsLog.reservedAt.desc().nullsLast()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<SmsSearchResponse> responses = logs.stream()
                .map(log -> {
                    LocalDateTime sendAt = null;

                    if(log.getSentAt() != null){
                        sendAt = log.getSentAt();
                    }

                    if(log.getSentAt() == null && log.getStatus() == SendStatus.PENDING && log.getSendReserveType() == SmsSendReserveType.RESERVE){
                        sendAt = log.getReservedAt();
                    }

                    return SmsSearchResponse.builder()
                            .logId(log.getId())
                            .smsForm(log.getSmsForm())
                            .defaultSmsType(log.getDefaultSmsType())
                            .reserveType(log.getSendReserveType())
                            .sender(log.getSenderNum())
                            .recipient(log.getPhoneNum())
                            .sendAt(sendAt)
                            .status(log.getStatus())
                            .build();
                })
                .collect(Collectors.toList());

        Long totalCount = jpaQueryFactory
                .select(smsLog.count())
                .from(smsLog)
                .where(builder)
                .fetchOne();

        return SearchResult.<SmsSearchResponse> builder()
                .totalCount(totalCount)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();
    }

    public SmsContentResponse getSmsContent(CustomUserDetails customUserDetails, Long logId){
        globalService.validateAdmin(customUserDetails);

        SmsLog log = smsLogRepository.findById(logId)
                .orElseThrow(() -> new CustomException(ErrorCode.SMS_LOG_NOT_FOUND));

        return SmsContentResponse.builder()
                .logId(log.getId())
                .content(log.getContent())
                .build();

    }

    public SearchResult<KakaoTemplateSearchResponse> searchKakaoTemplate(CustomUserDetails customUserDetails, KakaoTemplateSearchRequest request, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        QKakaoTemplate template = QKakaoTemplate.kakaoTemplate;
        BooleanBuilder builder = new BooleanBuilder();

        if(StringUtils.hasText(request.getKeyword()) && request.getKey() != null){
            switch (request.getKey()){
                case NAME -> builder.and(template.templateName.containsIgnoreCase(request.getKeyword()));
                case CODE -> builder.and(template.templateCode.containsIgnoreCase(request.getKeyword()));
                case CONTENT -> builder.and(template.content.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder allBuilder = new BooleanBuilder();
                    allBuilder.or(template.templateName.containsIgnoreCase(request.getKeyword()));
                    allBuilder.or(template.templateCode.containsIgnoreCase(request.getKeyword()));
                    allBuilder.or(template.content.containsIgnoreCase(request.getKeyword()));
                    builder.and(allBuilder);
                }
            }
        }

        if(request.getType() != null && request.getType() != SmsAutoType.ALL){
            builder.and(template.smsAutoType.eq(request.getType()));
        }

        if(request.getStatus() != null && request.getStatus() != KakaoTemplateStatus.ALL){
            builder.and(template.status.eq(request.getStatus()));
        }

        if(request.getRegisterAt() != null){
            if(request.getRegisterAt().getStartDate() != null){
                builder.and(template.registerAt.goe(request.getRegisterAt().getStartDate().atStartOfDay()));
            }
            if(request.getRegisterAt().getEndDate() != null){
                builder.and(template.registerAt.loe(request.getRegisterAt().getEndDate().atTime(23,59,59)));
            }
        }

        List<KakaoTemplate> templates = jpaQueryFactory
                .selectFrom(template)
                .where(builder)
                .orderBy(template.registerAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<KakaoTemplateSearchResponse> responses = templates.stream()
                .map(t -> KakaoTemplateSearchResponse.builder()
                        .templateId(t.getId())
                        .templateCode(t.getTemplateCode())
                        .templateName(t.getTemplateName())
                        .smsAutoType(t.getSmsAutoType())
                        .content(t.getContent())
                        .registerDate(t.getRegisterAt().toLocalDate())
                        .status(t.getStatus())
                        .build()
                ).collect(Collectors.toList());

        Long totalCount = jpaQueryFactory
                .select(template.count())
                .from(template)
                .where(builder)
                .fetchOne();

        return SearchResult.<KakaoTemplateSearchResponse> builder()
                .totalCount(totalCount)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();
    }

    public SearchResult<KakaoLogSearchResponse> searchKakaoLog(CustomUserDetails customUserDetails, KakaoLogSearchRequest request, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        QKakaoLog log = QKakaoLog.kakaoLog;
        BooleanBuilder builder = new BooleanBuilder();

        if(StringUtils.hasText(request.getKeyword()) && request.getKey() != null){
            switch (request.getKey()){
                case RECIPIENT -> builder.and(log.recipientPhone.containsIgnoreCase(request.getKeyword()));
                case CONTENT -> builder.and(log.sendContent.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder allBuilder = new BooleanBuilder();
                    allBuilder.or(log.recipientPhone.containsIgnoreCase(request.getKeyword()));
                    allBuilder.or(log.sendContent.containsIgnoreCase(request.getKeyword()));
                    builder.and(allBuilder);
                }
            }
        }

        if(request.getStatus() != null){
            if(request.getStatus() == SendStatus.SENT){
                builder.and(log.sendStatus.eq(SendStatus.SENT));
            }
            if(request.getStatus() == SendStatus.FAILED){
                builder.and(log.sendStatus.eq(SendStatus.FAILED));
            }
        }

        if(request.getSentAt() != null){
            if(request.getSentAt().getStartDate() != null){
                builder.and(log.sentAt.goe(request.getSentAt().getStartDate().atStartOfDay()));
            }
            if(request.getSentAt().getEndDate() != null){
                builder.and(log.sentAt.loe(request.getSentAt().getEndDate().atTime(23,59,59)));
            }
        }

        List<KakaoLog> logs = jpaQueryFactory
                .selectFrom(log)
                .where(builder)
                .orderBy(log.sentAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<KakaoLogSearchResponse> responses = logs.stream()
                .map(l -> KakaoLogSearchResponse.builder()
                        .logId(l.getId())
                        .recipientPhone(l.getRecipientPhone())
                        .templateName(l.getTemplateName())
                        .sentAt(l.getSentAt())
                        .failReason(l.getFailReason())
                        .sendStatus(l.getSendStatus())
                        .build()
                ).collect(Collectors.toList());

        Long totalCount = jpaQueryFactory
                .select(log.count())
                .from(log)
                .where(builder)
                .fetchOne();

        return SearchResult.<KakaoLogSearchResponse> builder()
                .totalCount(totalCount)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();
    }
}
