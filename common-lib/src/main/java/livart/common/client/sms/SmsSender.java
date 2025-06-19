package livart.common.client.sms;

import com.google.common.collect.Lists;
import livart.common.domain.alarm.entity.SmsSetting;
import livart.common.domain.alarm.repository.SmsSettingRepository;
import livart.common.domain.user.entity.User;
import livart.common.dto.enums.alarm.DefaultSmsType;
import livart.common.dto.enums.alarm.SendStatus;
import livart.common.dto.enums.alarm.SmsForm;
import livart.common.dto.enums.alarm.SmsSendReserveType;
import livart.common.dto.response.SmsSendResult;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.log.entity.SmsLog;
import livart.common.log.repository.SmsLogRepository;
import livart.common.service.GlobalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Balance;
import net.nurigo.sdk.message.model.Message;
import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.MessageType;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.services.ses.endpoints.internal.Value;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsSender {

    private final SmsSettingRepository smsSettingRepository;
    private DefaultMessageService messageService;
    private String currentApiKey;
    private String currentApiSecret;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final int MAX_SMS_BYTES = 90;
    private static final int MAX_RETRY_COUNT = 3;


    private final GlobalService globalService;
    private final SmsLogRepository smsLogRepository;

    public SmsSetting updateRemainingPoint() {
        SmsSetting setting = smsSettingRepository.findFirstByIsActiveTrue()
                .orElseThrow(() -> new CustomException(ErrorCode.SMS_SETTING_NOT_FOUND));

        try {
            initializeMessageService(setting.getSmsApiKey(), setting.getSmsApiSecret());
            Balance balance = this.messageService.getBalance();
            BigDecimal point = new BigDecimal(balance.getPoint());
            setting.updatePoint(point);
        } catch (Exception e) {
            log.warn("잔여 포인트 갱신 실패. 이전 값을 유지합니다.");
        }

        return smsSettingRepository.save(setting);
    }

    public BigDecimal remainingPoint(String apiKey, String apiSecret) {
        try {
            initializeMessageService(apiKey, apiSecret);
            Balance balance = this.messageService.getBalance();
            return new BigDecimal(balance.getPoint());
        } catch (Exception e) {
            log.warn("잔여 포인트 조회에 실패하셨습니다", e);
            throw new CustomException(ErrorCode.SMS_BALANCE_FETCH_FAILED);
        }
    }
    @Transactional
    public void sendSMS(String to, String certificationCode) {
        SmsSetting setting = updateRemainingPoint();

        String apiKey = setting.getSmsApiKey();
        String apiSecret = setting.getSmsApiSecret();

        initializeMessageService(apiKey, apiSecret);

        Message message = new Message();
        message.setFrom(setting.getSenderNum());
        message.setTo(to);
        message.setText(certificationCode);

        this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }

    @Transactional
    public void sendOne(String to, String from, String messageText, DefaultSmsType defaultSmsType) {
        SmsSetting setting = updateRemainingPoint();

        String apiKey = setting.getSmsApiKey();
        String apiSecret = setting.getSmsApiSecret();

        initializeMessageService(apiKey, apiSecret);

        if (defaultSmsType == DefaultSmsType.LMS) {
            Message lms = new Message();
            lms.setFrom(from);
            lms.setTo(to);
            lms.setText(messageText);
            lms.setType(MessageType.LMS);
            lms.setSubject("알림");
            this.messageService.sendOne(new SingleMessageSendingRequest(lms));

        } else if (defaultSmsType == DefaultSmsType.ONLY_90_BYTES) {
            String trimmed = trimTo90Bytes(messageText);
            Message sms = new Message();
            sms.setFrom(from);
            sms.setTo(to);
            sms.setText(trimmed);
            sms.setType(MessageType.SMS);
            this.messageService.sendOne(new SingleMessageSendingRequest(sms));

        } else if (defaultSmsType == DefaultSmsType.DIVISION_SMS) {
            List<String> parts = splitBy90Bytes(messageText);
            for (String part : parts) {
                Message sms = new Message();
                sms.setFrom(from);
                sms.setTo(to);
                sms.setText(part);
                sms.setType(MessageType.SMS);
                this.messageService.sendOne(new SingleMessageSendingRequest(sms));
            }
        }
    }


    @Transactional
    public SmsSendResult sendSmsAll(List<String> phoneNums, SmsForm smsForm, Long templateId, SmsSendReserveType reserveType,
                                    DefaultSmsType defaultSmsType, String messageText, LocalDateTime reserveDateTime) {

        SmsSetting setting = updateRemainingPoint();

        String apiKey = setting.getSmsApiKey();
        String apiSecret = setting.getSmsApiSecret();
        String sender = setting.getSenderNum();

        List<String> successList = new ArrayList<>();
        List<String> failureList = new ArrayList<>();
        Long reserveCount = 0L;
        Map<String, String> errorMap = new HashMap<>();

        initializeMessageService(apiKey, apiSecret);

        if (reserveType == SmsSendReserveType.RESERVE) {
            globalService.logSmsSend(
                    smsForm,
                    templateId,
                    sender,
                    phoneNums,
                    messageText,
                    reserveType,
                    defaultSmsType,
                    SendStatus.PENDING,
                    "예약중인 메세지입니다.",
                    null,
                    reserveDateTime
            );
            reserveCount = (long) phoneNums.size();

        } else if (reserveType == SmsSendReserveType.INSTANT){

            LocalDateTime now = LocalDateTime.now();

            for (String phone : phoneNums) {
                try {
                    if (defaultSmsType == DefaultSmsType.LMS) {
                        Message lms = new Message();
                        lms.setFrom(sender);
                        lms.setTo(phone);
                        lms.setText(messageText);
                        lms.setType(MessageType.LMS);
                        lms.setSubject("Livart 발송 LMS 입니다.");
                        this.messageService.sendOne(new SingleMessageSendingRequest(lms));
                    }

                    else if (defaultSmsType == DefaultSmsType.ONLY_90_BYTES) {
                        String trimmed = trimTo90Bytes(messageText);
                        Message sms = new Message();
                        sms.setFrom(sender);
                        sms.setTo(phone);
                        sms.setText(trimmed);
                        sms.setType(MessageType.SMS);
                        this.messageService.sendOne(new SingleMessageSendingRequest(sms));
                    }

                    else if (defaultSmsType == DefaultSmsType.DIVISION_SMS) {
                        List<String> parts = splitBy90Bytes(messageText);
                        for (String part : parts) {
                            Message sms = new Message();
                            sms.setFrom(sender);
                            sms.setTo(phone);
                            sms.setText(part);
                            sms.setType(MessageType.SMS);
                            this.messageService.sendOne(new SingleMessageSendingRequest(sms));
                        }
                    }
                    successList.add(phone);
                } catch (Exception e) {
                    failureList.add(phone);
                    errorMap.put(phone, e.getMessage());
                }
            }

            // 성공 로그
            if (!successList.isEmpty()) {
                globalService.logSmsSend(
                        smsForm,
                        templateId,
                        sender,
                        successList,
                        messageText,
                        reserveType,
                        defaultSmsType,
                        SendStatus.SENT,
                        null,
                        now,
                        null
                );
            }

            // 실패 로그
            if (!failureList.isEmpty()) {
                for (String phone : failureList) {
                    globalService.logSmsSend(
                            smsForm,
                            templateId,
                            sender,
                            List.of(phone),
                            messageText,
                            reserveType,
                            defaultSmsType,
                            SendStatus.FAILED,
                            errorMap.get(phone),
                            now,
                            null
                    );
                }
            }
        }

        Long totalTargetCount = (long) phoneNums.size();
        Long successCount = (long) successList.size();
        Long failCount = (long) failureList.size();

        return SmsSendResult.builder()
                .recipientCount(totalTargetCount)
                .successCount(successCount)
                .reserveCount(reserveCount)
                .failCount(failCount)
                .build();
    }


    private int getByteLength(String text) {
        return text.getBytes(StandardCharsets.UTF_8).length;
    }

    private String trimTo90Bytes(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        if (bytes.length <= MAX_SMS_BYTES) return text;

        int byteCount = 0;
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            byteCount += String.valueOf(c).getBytes(StandardCharsets.UTF_8).length;
            if (byteCount > MAX_SMS_BYTES) break;
            sb.append(c);
        }
        return sb.toString();
    }

    private List<String> splitBy90Bytes(String text) {
        List<String> parts = new ArrayList<>();
        StringBuilder part = new StringBuilder();
        int byteCount = 0;

        for (char c : text.toCharArray()) {
            byte[] cBytes = String.valueOf(c).getBytes(StandardCharsets.UTF_8);
            if (byteCount + cBytes.length > MAX_SMS_BYTES) {
                parts.add(part.toString());
                part = new StringBuilder();
                byteCount = 0;
            }
            part.append(c);
            byteCount += cBytes.length;
        }
        if (part.length() > 0) {
            parts.add(part.toString());
        }
        return parts;
    }

    @Scheduled(fixedDelay = 60000) // 매 1분마다 실행
    public void processReservedSms() {
        log.info("[ProcessReservedSms] 스케줄러 실행됨 - {}", LocalDateTime.now());
        LocalDateTime now = LocalDateTime.now();

        SmsSetting setting = updateRemainingPoint();

        List<SmsLog> targets = smsLogRepository.findByStatusAndSendReserveTypeAndReservedAtLessThanEqualAndRetryCountLessThanEqual(
                SendStatus.PENDING,
                SmsSendReserveType.RESERVE,
                now,
                MAX_RETRY_COUNT
        );

        if(!targets.isEmpty()) {

            List<List<SmsLog>> batches = Lists.partition(targets, 50);

            for (List<SmsLog> batch : batches) {
                for (SmsLog log : batch) {
                    try {
                        sendOne(log.getPhoneNum(), setting.getSenderNum(), log.getContent(), log.getDefaultSmsType());
                        log.update(SendStatus.SENT, null, setting.getSenderNum(), LocalDateTime.now());
                    } catch (Exception e) {
                        log.setRetryCount(log.getRetryCount() + 1);

                        if (log.getRetryCount() > 3) {
                            log.setStatus(SendStatus.FINAL_FAILED);
                        } else {
                            log.setStatus(SendStatus.PENDING);
                        }

                        log.setErrorMessage(e.getMessage());
                        log.setSenderNum(setting.getSenderNum());
                    }
                }
                smsLogRepository.saveAll(batch);
            }
        }
    }

    private void initializeMessageService(String apiKey, String apiSecret) {
        if (messageService == null || !apiKey.equals(currentApiKey) || !apiSecret.equals(currentApiSecret)) {
            this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
            this.currentApiKey = apiKey;
            this.currentApiSecret = apiSecret;
        }
    }

    private String createHmacSignature(String secret, String message) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC signature", e);
        }
    }

    private String getCurrentDateISO8601() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now());
    }

    public BigDecimal fetchCoolSmsCashBalance(String apiKey, String apiSecret) {
        String url = "https://api.coolsms.co.kr/cash/v1/balance";

        String date = getCurrentDateISO8601();
        String salt = UUID.randomUUID().toString();
        String signature = createHmacSignature(apiSecret, date + salt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", String.format("HMAC-SHA256 apiKey=%s, date=%s, salt=%s, signature=%s",
                apiKey, date, salt, signature
        ));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        JSONObject json = new JSONObject(response.getBody());
        int cash = json.getInt("cash"); // cash 필드 사용

        return BigDecimal.valueOf(cash);
    }

}

