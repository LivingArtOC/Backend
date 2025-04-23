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
@Tag(name = "회사 정보 및 약관 정책 설정 관련 API")
@RequestMapping("api/erp/setting/policy")
public class PolicyController {

    private final PolicyService policyService;

    @PostMapping("/company")
    @Operation(summary = "회사 정보 저장 API, 토큰 O")
    public ResponseEntity<ApiResponse<CompanyInfoResponse>> saveDefault(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                        @RequestBody CompanyInfoRequest request){
        CompanyInfoResponse response = policyService.saveDefault(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/company")
    @Operation(summary = "회사 정보 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<CompanyInfoResponse>> getDefault(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        CompanyInfoResponse response = policyService.getDefault(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/company")
    @Operation(summary = "회사 정보 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<CompanyInfoResponse>> updateDefault(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                        @RequestBody CompanyInfoRequest request){
        CompanyInfoResponse response = policyService.updateDefault(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/use")
    @Operation(summary = "이용 약관 저장 API, 토큰 O")
    public ResponseEntity<ApiResponse<UsePolicyResponse>> saveUsePolicy(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                      @RequestBody UsePolicyRequest request){
        UsePolicyResponse response = policyService.saveUsePolicy(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/use")
    @Operation(summary = "이용 약관 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<UsePolicyResponse>> getUsePolicy(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        UsePolicyResponse response = policyService.getUsePolicy(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/use")
    @Operation(summary = "이용 약관 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<UsePolicyResponse>> updateUsePolicy(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                          @RequestBody UsePolicyRequest request){
        UsePolicyResponse response = policyService.updateUsePolicy(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/course")
    @Operation(summary = "개인정보 처리방침 저장 API, 토큰 O")
    public ResponseEntity<ApiResponse<CourseResponse>> saveCourse(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                        @RequestBody CourseRequest request){
        CourseResponse response = policyService.saveCourse(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/course")
    @Operation(summary = "개인정보 처리방침 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourse(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        CourseResponse response = policyService.getCourse(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/course")
    @Operation(summary = "개인정보 처리방침 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                          @RequestBody CourseRequest request){
        CourseResponse response = policyService.updateCourse(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/others")
    @Operation(summary = "나머지 약관 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<TermsResponse>>> getTerms(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<TermsResponse> response = policyService.getTerms(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{termId}")
    @Operation(summary = "특정 약관 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<TermsResponse>> updateTerm(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                    @RequestBody TermsRequest request,
                                                                  @PathVariable Long termId){
        TermsResponse response = policyService.updateTerm(customUserDetails, request, termId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

}
