package livart.erp.domain.alarm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.Auth.CustomUserDetails;
import livart.common.client.sms.SmsSender;
import livart.common.dto.request.SmsAutoDto;
import livart.common.dto.response.ApiResponse;
import livart.common.dto.response.SendResult;
import livart.common.dto.response.SmsSendResult;
import livart.common.mapper.SearchResult;
import livart.erp.domain.alarm.dto.request.*;
import livart.erp.domain.alarm.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "알림 관련 API", description = "✅ 개발 완료")
@RequestMapping("/api/erp/alarm")
public class AlarmController {
    private final AlarmService alarmService;
    private final SmsSender smsSender;

    @PutMapping("/sms/point")
    @Operation(summary = "✅ SMS/알림톡 SMS 포인트 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<AlarmPointResponse>> getPoint(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        AlarmPointResponse response = alarmService.getPoint(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/all/setting")
    @Operation(summary = "✅ SMS/알림톡 통합 설정 저장 & 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<SmsKakaoSettingDto>> updateSetting(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                         @RequestBody SmsKakaoSettingDto request){
        SmsKakaoSettingDto response = alarmService.updateSetting(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/all/setting")
    @Operation(summary = "✅ SMS/알림톡 통합 설정 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<SmsKakaoSettingDto>> getSetting(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        SmsKakaoSettingDto response = alarmService.getSetting(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/sms/setting")
    @Operation(summary = "✅ SMS 발송 설정 저장 & 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<SmsSettingResponse>> updateSmsSetting(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                            @RequestBody SmsUpdateRequest request){
        SmsSettingResponse response = alarmService.updateSmsSetting(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/sms/setting")
    @Operation(summary = "✅ SMS 발송 설정 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<SmsSettingResponse>> getSmsSetting(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        SmsSettingResponse response = alarmService.getSmsSetting(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/email/setting")
    @Operation(summary = "✅ EMAIL 발송 설정 저장 & 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<EmailSettingDto>> updateEmailSetting(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                         @RequestBody EmailSettingDto request){
        EmailSettingDto response = alarmService.updateEmailSetting(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/email/setting")
    @Operation(summary = "✅ EMAIL 발송 설정 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<EmailSettingDto>> getEmailSetting(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        EmailSettingDto response = alarmService.getEmailSetting(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/member/search")
    @Operation(summary = "✅ 알림 대상 검색 목적의 회원 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<MemberSearchResponse>>> searchMember(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody MemberAddRequest request,
            @PageableDefault(page = 0, size = 50, sort = "createdAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable){

        SearchResult<MemberSearchResponse> response = alarmService.searchMember(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/email/send/info")
    @Operation(summary = "✅ 메일 직접 발송 시 기본 정보(발송자 이메일, 수신 거부 회원 수) 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<EmailInfoResponse>> getEmailInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        EmailInfoResponse response = alarmService.getEmailInfo(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/email/send")
    @Operation(summary = "✅ EMAIL 직접 발송 API, 토큰 O")
    public ResponseEntity<ApiResponse<SendResult>> sendEmail(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                             @RequestBody EmailSendRequest request){
        SendResult result = alarmService.sendEmail(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/email/auto/{type}")
    @Operation(summary = "✅ 자동 메일 설정 각각 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<EmailAutoResponse>> getAutoEmailSetting(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                       @PathVariable String type){
        EmailAutoResponse response = alarmService.getAutoEmailSetting(customUserDetails, type);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/email/auto/{type}")
    @Operation(summary = "✅ 자동 메일 설정 각각 저장 & 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<EmailAutoResponse>> updateAutoEmailSetting(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                              @RequestBody EmailAutoRequest request,
                                                                              @PathVariable String type){
        EmailAutoResponse response = alarmService.updateAutoEmailSetting(customUserDetails,request, type);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/email/send/search")
    @Operation(summary = "✅ 메일 발송 내역 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<EmailSearchResponse>>> searchEmailLog(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody EmailSearchRequest request,
            @PageableDefault(page = 0, size = 50, sort = "sentAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable){

        SearchResult<EmailSearchResponse> response = alarmService.searchEmailLog(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/email/send/search/{logId}")
    @Operation(summary = "✅ 메일 본문 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<EmailContentResponse>> getEmailContent(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                              @PathVariable Long logId){
        EmailContentResponse response = alarmService.getEmailContent(customUserDetails, logId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/sms/auto")
    @Operation(summary = "✅ 자동 SMS 설정 각각 저장 & 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<SmsAutoDto>>> updateAutoSmsSetting(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                              @RequestBody List<SmsAutoDto> request){
        List<SmsAutoDto> response = alarmService.updateAutoSmsSetting(customUserDetails,request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/sms/auto/{type}")
    @Operation(summary = "✅ 자동 SMS 설정 상단 탭별 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<SmsAutoDto>>> getAutoSmsSetting(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                              @PathVariable String type){
        List<SmsAutoDto> response = alarmService.getAutoSmsSetting(customUserDetails, type);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/sms/send/info")
    @Operation(summary = "✅ SMS 개별/전체 발송 시 기본 정보 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<SmsInfoResponse>> getSmsInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        SmsInfoResponse response = alarmService.getSmsInfo(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/sms/send")
    @Operation(summary = "✅ SMS 개별/전체 발송 API, 토큰 O")
    public ResponseEntity<ApiResponse<SmsSendResult>> sendSms(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                              @RequestBody SmsSendRequest request){
        SmsSendResult result = alarmService.sendSms(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/point")
    @Operation(summary = "SMS 포인트 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<BigDecimal>> sendSms(){
        return ResponseEntity.ok(ApiResponse.ok(smsSender.fetchCoolSmsCashBalance("NCSXENMFFNALME0Y", "STOMCHZGCBYVNIEQYZRNCEDD39AYNQGN")));
    }

    @PostMapping("/sms/content")
    @Operation(summary = "✅ SMS 양식 저장 API, 토큰 O")
    public ResponseEntity<ApiResponse<ContentResponse>> saveContent(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                       @RequestBody ContentRequest request){
        ContentResponse response = alarmService.saveContent(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/sms/content")
    @Operation(summary = "✅ SMS 양식 리스트 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<ContentResponse>>> getContent(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<ContentResponse> response = alarmService.getContent(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/sms/content/{contentId}")
    @Operation(summary = "✅ SMS 양식 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<ContentResponse>> updateContent(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                    @RequestBody ContentRequest request, @PathVariable Long contentId){
        ContentResponse response = alarmService.updateContent(customUserDetails, request, contentId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/sms/content/{contentId}")
    @Operation(summary = "✅ SMS 양식 삭제 API, 토큰 O")
    public ResponseEntity<ApiResponse<String>> deleteContent(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                           @PathVariable Long contentId){
        alarmService.deleteContent(customUserDetails, contentId);
        return ResponseEntity.ok(ApiResponse.ok("성공적으로 삭제되었습니다."));
    }

    @PostMapping("/sms/send/search")
    @Operation(summary = "✅ SMS 발송 내역 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<SmsSearchResponse>>> searchSmsLog(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody SmsSearchRequest request,
            @PageableDefault(page = 0, size = 50, sort = "createdAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable){

        SearchResult<SmsSearchResponse> response = alarmService.searchSmsLog(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/sms/{logId}")
    @Operation(summary = "✅ SMS 본문 내용 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<SmsContentResponse>> getSmsContent(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                               @PathVariable Long logId){
        SmsContentResponse response = alarmService.getSmsContent(customUserDetails, logId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/kakao/send/search")
    @Operation(summary = "KAKAO 템플릿 목록 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<KakaoTemplateSearchResponse>>> searchKakaoTemplate(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody KakaoTemplateSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "registerAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable){

        SearchResult<KakaoTemplateSearchResponse> response = alarmService.searchKakaoTemplate(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/kakao/log/send/search")
    @Operation(summary = "KAKAO 발송 내역 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<KakaoLogSearchResponse>>> searchKakaoLog(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody KakaoLogSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "sentAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable){

        SearchResult<KakaoLogSearchResponse> response = alarmService.searchKakaoLog(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
