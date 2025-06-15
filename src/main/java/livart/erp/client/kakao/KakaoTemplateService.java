package livart.erp.client.kakao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import livart.common.domain.alarm.entity.KakaoTemplate;
import livart.common.domain.alarm.entity.SmsSetting;
import livart.common.domain.alarm.repository.KakaoTemplateRepository;
import livart.common.domain.alarm.repository.SmsSettingRepository;
import livart.common.dto.enums.alarm.KakaoTemplateStatus;
import livart.common.dto.enums.alarm.SendStatus;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.log.entity.KakaoLog;
import livart.common.log.repository.KakaoLogRepository;
import livart.erp.client.kakao.KakaoTemplateRegisterRequest;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoTemplateService {

    @Value("${coolsms.api.base-url}")
    private String baseUrl;

    @Value("${kakao.pf-id}")
    private String pfId;

    private final KakaoTemplateRepository kakaoTemplateRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private final SmsSettingRepository smsSettingRepository;
    private final KakaoLogRepository kakaoLogRepository;

    public void registerTemplate(KakaoTemplateRegisterRequest request) {
        String url = baseUrl + "/kakao/v1/templates";

        Map<String, Object> payload = new HashMap<>();
        payload.put("pfId", pfId);
        payload.put("templateName", request.getTemplateName());
        payload.put("template", request.getContent());
        payload.put("templateType", request.getTemplateType());
        payload.put("ad", request.getIsAdv());
        payload.put("securityFlag", request.getSecurityFlag());
        payload.put("language", request.getLanguage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", generateHmacHeader("POST", "/kakao/v1/templates", payload));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> body = response.getBody();
            String templateCode = (String) body.get("templateCode");

            if (templateCode == null) {
                throw new RuntimeException("CoolSMS 응답에 templateCode 없음: " + body);
            }

            KakaoTemplate saved = KakaoTemplate.builder()
                    .templateCode(templateCode)
                    .templateName(request.getTemplateName())
                    .content(request.getContent())
                    .isAdv(request.getIsAdv())
                    .templateType(request.getTemplateType())
                    .language(request.getLanguage())
                    .securityFlag(request.getSecurityFlag())
                    .smsDivision(request.getSmsDivision())
                    .smsAutoType(request.getSmsAutoType())
                    .status(KakaoTemplateStatus.PENDING)
                    .registerAt(LocalDateTime.now())
                    .build();

            kakaoTemplateRepository.save(saved);
        } else {
            log.error("CoolSMS 템플릿 등록 실패. 응답: {}", response.getBody());
            throw new RuntimeException("템플릿 등록 실패: " + response.getBody());
        }
    }

    public String generateHmacHeader(String method, String path, Map<String, Object> body) {
        try {
            SmsSetting setting = smsSettingRepository.findFirstByIsActiveTrue()
                    .orElseThrow(() -> new CustomException(ErrorCode.SMS_SETTING_NOT_FOUND));

            String salt = UUID.randomUUID().toString();
            long timestamp = System.currentTimeMillis();

            String bodyString = new ObjectMapper().writeValueAsString(body);
            String signatureData = method + path + timestamp + salt + bodyString;

            SecretKeySpec key = new SecretKeySpec(setting.getSmsApiSecret().getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);
            byte[] hmacBytes = mac.doFinal(signatureData.getBytes());
            String signature = Base64.getEncoder().encodeToString(hmacBytes);

            return String.format("HMAC-SHA256 apiKey=%s, date=%d, salt=%s, signature=%s",
                    setting.getSmsApiKey(), timestamp, salt, signature);

        } catch (Exception e) {
            throw new RuntimeException("HMAC 생성 실패", e);
        }
    }

    @Transactional
    public void syncTemplateStatuses() {
        List<KakaoTemplate> pendingTemplates = kakaoTemplateRepository.findAllByStatus(KakaoTemplateStatus.PENDING);

        for (KakaoTemplate template : pendingTemplates) {
            try {
                String url = baseUrl + "/kakao/v1/templates/" + template.getTemplateCode();

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", generateHmacHeader("GET", "/kakao/v1/templates/" + template.getTemplateCode(), new HashMap<>()));

                HttpEntity<Void> entity = new HttpEntity<>(headers);

                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    Map<String, Object> body = response.getBody();
                    String status = (String) body.get("templateStatus");
                    if (status == null) {
                        log.warn("템플릿 상태 없음 (templateCode={}): 응답={}", template.getTemplateCode(), body);
                        continue;
                    }

                    switch (status) {
                        case "승인" -> {
                            if (template.getStatus() != KakaoTemplateStatus.APPROVED) {
                                log.info("템플릿 승인됨: {}", template.getTemplateCode());
                                template.approve(LocalDateTime.now());
                            }
                        }
                        case "반려" -> {
                            if (template.getStatus() != KakaoTemplateStatus.REJECTED) {
                                log.info("템플릿 반려됨: {}", template.getTemplateCode());
                                String reason = (String) body.get("reason");
                                template.reject(reason != null ? reason : "사유 미제공");
                            }
                        }
                    }

                    kakaoTemplateRepository.save(template);
                } else {
                    log.warn("템플릿 상태 조회 실패: {}", response.getBody());
                }

            } catch (Exception e) {
                log.error("템플릿 상태 동기화 중 오류 발생 (templateCode={}): {}", template.getTemplateCode(), e.getMessage());
            }
        }
    }

    @Scheduled(cron = "0 0 * * * *") // 매 시 정각마다 동기화
    public void scheduledSyncTemplateStatuses() {
        syncTemplateStatuses();
    }

    public void sendAlarmTalk(Long templateId, String content, String recipientPhone) {
        KakaoTemplate template = kakaoTemplateRepository.findById(templateId)
                .orElseThrow(() -> new CustomException(ErrorCode.TEMPLATE_NOT_FOUND));

        if (template.getStatus() != KakaoTemplateStatus.APPROVED) {
            throw new CustomException(ErrorCode.TEMPLATE_NOT_APPROVED);
        }

        SmsSetting smsSetting = smsSettingRepository.findFirstByIsActiveTrue()
                .orElseThrow(() -> new CustomException(ErrorCode.SMS_SETTING_NOT_FOUND));

        // 버튼 파싱
        List<Map<String, Object>> buttons = parseButtonJson(template.getButton());

        // 발송 payload 구성
        Map<String, Object> kakaoOptions = new HashMap<>();
        kakaoOptions.put("pfId", pfId);
        kakaoOptions.put("templateId", template.getTemplateCode());
        if (buttons != null && !buttons.isEmpty()) {
            kakaoOptions.put("buttons", buttons);
        }

        Map<String, Object> payload = Map.of(
                "to", recipientPhone,
                "from", smsSetting.getSenderNum(),
                "text", content,
                "type", "ATA",
                "kakaoOptions", kakaoOptions
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", generateHmacHeader("POST", "/messages/v4/send", payload));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.solapi.com/messages/v4/send", entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                // 로그 저장 - 성공
                kakaoLogRepository.save(KakaoLog.builder()
                        .templateId(templateId)
                        .templateName(template.getTemplateName())
                        .recipientPhone(recipientPhone)
                        .sendContent(content)
                        .sendStatus(SendStatus.SENT)
                        .sentAt(LocalDateTime.now())
                        .build());
            } else {
                // 로그 저장 - 실패
                kakaoLogRepository.save(KakaoLog.builder()
                        .templateId(templateId)
                        .templateName(template.getTemplateName())
                        .recipientPhone(recipientPhone)
                        .sendContent(content)
                        .sendStatus(SendStatus.FAILED)
                        .failReason(response.getBody() != null ? response.getBody().toString() : "Unknown")
                        .sentAt(LocalDateTime.now())
                        .build());
            }

        } catch (Exception e) {
           kakaoLogRepository.save(KakaoLog.builder()
                    .templateId(templateId)
                    .templateName(template.getTemplateName())
                    .recipientPhone(recipientPhone)
                    .sendContent(content)
                    .sendStatus(SendStatus.FAILED)
                    .failReason(e.getMessage())
                    .sentAt(LocalDateTime.now())
                    .build());

            throw new RuntimeException("알림톡 발송 중 오류 발생", e);
        }
    }

    private List<Map<String, Object>> parseButtonJson(String json) {
        if (!StringUtils.hasText(json)) return null;
        try {
            return new ObjectMapper().readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("버튼 JSON 파싱 실패", e);
        }
    }

}

