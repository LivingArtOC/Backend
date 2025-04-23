package livart.erp.domain.facility.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.Auth.CustomUserDetails;
import livart.common.dto.enums.Provider;
import livart.common.dto.response.ApiResponse;
import livart.erp.domain.facility.dto.request.SocialRequest;
import livart.erp.domain.facility.dto.response.SocialResponse;
import livart.erp.domain.facility.service.SocialAPIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/erp/facility")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "소셜 앱 등록 관련 API")
public class SocialAPIController {

    private final SocialAPIService socialAPIService;

    @PutMapping("/social-api")
    @Operation(summary = "소셜 앱 설정 수정 API", description = "토큰 O")
    public ResponseEntity<ApiResponse<SocialResponse>> updateSetting(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                  @RequestBody SocialRequest socialRequest){
        SocialResponse response = socialAPIService.updateSetting(customUserDetails, socialRequest);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.ok(response));
    }

    @GetMapping("/{provider}")
    @Operation(summary = "소셜 앱 설정 조회 API", description = "토큰 O")
    public ResponseEntity<ApiResponse<SocialResponse>> getSocialSetting(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                        @PathVariable String provider){
        SocialResponse response = socialAPIService.getSetting(customUserDetails, provider);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

}
