package livart.erp.domain.defaultSetting.policy;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.Auth.CustomUserDetails;
import livart.common.dto.response.ApiResponse;
import livart.erp.domain.defaultSetting.policy.dto.request.CompanyInfoRequest;
import livart.erp.domain.defaultSetting.policy.dto.request.CourseRequest;
import livart.erp.domain.defaultSetting.policy.dto.request.TermsRequest;
import livart.erp.domain.defaultSetting.policy.dto.request.UsePolicyRequest;
import livart.erp.domain.defaultSetting.policy.dto.response.CompanyInfoResponse;
import livart.erp.domain.defaultSetting.policy.dto.response.CourseResponse;
import livart.erp.domain.defaultSetting.policy.dto.response.TermsResponse;
import livart.erp.domain.defaultSetting.policy.dto.response.UsePolicyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "기본 설정 - 기본 정책 설정 관련 API", description = "✅ 개발 완료")
@RequestMapping("api/erp/setting/policy")
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping("/company")
    @Operation(summary = "✅ 회사 정보 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<CompanyInfoResponse>> getDefault(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        CompanyInfoResponse response = policyService.getDefault(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/company")
    @Operation(summary = "✅ 회사 정보 저장 & 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<CompanyInfoResponse>> updateDefault(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                        @RequestBody CompanyInfoRequest request){
        CompanyInfoResponse response = policyService.updateDefault(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/use-policy")
    @Operation(summary = "✅ 이용 약관 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<UsePolicyResponse>> getUsePolicy(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        UsePolicyResponse response = policyService.getUsePolicy(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/use-policy")
    @Operation(summary = "✅ 이용 약관 저장 & 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<UsePolicyResponse>> updateUsePolicy(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                          @RequestBody UsePolicyRequest request){
        UsePolicyResponse response = policyService.updateUsePolicy(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/course")
    @Operation(summary = "✅ 개인정보 처리방침 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourse(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        CourseResponse response = policyService.getCourse(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/course")
    @Operation(summary = "✅ 개인정보 처리방침 저장 & 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                          @RequestBody CourseRequest request){
        CourseResponse response = policyService.updateCourse(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/others")
    @Operation(summary = "✅ 나머지 약관 일괄 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<TermsResponse>>> getTerm(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<TermsResponse> response = policyService.getTerm(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/others")
    @Operation(summary = "✅ 나머지 약관 개별 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<TermsResponse>> updateTerm(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                    @RequestBody TermsRequest request){
        TermsResponse response = policyService.updateTerm(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

}
