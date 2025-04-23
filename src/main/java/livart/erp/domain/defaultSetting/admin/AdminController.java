package livart.erp.domain.defaultSetting.admin;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import livart.common.Auth.CustomUserDetails;
import livart.common.dto.enums.ActionType;
import livart.common.dto.request.ValidateLoginIdRequest;
import livart.common.dto.response.ApiResponse;
import livart.common.mapper.SearchResult;
import livart.common.service.GlobalService;
import livart.erp.domain.defaultSetting.admin.dto.request.AdminLogSearchRequest;
import livart.erp.domain.defaultSetting.admin.dto.request.AdminRequest;
import livart.erp.domain.defaultSetting.admin.dto.request.AdminSearchRequest;
import livart.erp.domain.defaultSetting.admin.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "ADMIN + 관리 정책 설정 관련 API")
@RequestMapping("api/erp/setting/admin")
public class AdminController {

    private final AdminService adminService;
    private final GlobalService globalService;

    @PostMapping("/register")
    @Operation(summary = "운영자 등록 API, 토큰 O")
    public ResponseEntity<ApiResponse<AdminResponse>> createAdmin(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                  @RequestBody AdminRequest request){
        AdminResponse response = adminService.createAdmin(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/{adminId}")
    @Operation(summary = "운영자 세부 정보 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<AdminResponse>> getAdmin(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                               @PathVariable Long adminId,
                                                               HttpServletRequest httpServletRequest){
        AdminResponse response = adminService.getAdmin(customUserDetails, adminId);
        globalService.log(
                customUserDetails.getId(),
                customUserDetails.getUsername(),
                ActionType.GET,
                httpServletRequest.getRequestURI(),
                "admin",
                adminId,
                httpServletRequest.getRemoteAddr()
        );
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{adminId}")
    @Operation(summary = "운영자 세부 정보 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<AdminResponse>> updateAdmin(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                  @RequestBody AdminRequest request,
                                                                  @PathVariable Long adminId,
                                                                  HttpServletRequest httpServletRequest){
        AdminResponse response = adminService.updateAdmin(customUserDetails, request, adminId);

        globalService.log(
                customUserDetails.getId(),
                customUserDetails.getUsername(),
                ActionType.UPDATE,
                httpServletRequest.getRequestURI(),
                "admin",
                adminId,
                httpServletRequest.getRemoteAddr()
        );

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/validate")
    @Operation(summary = "관리자 전용 아이디 중복 확인 API, 토큰 O")
    public ResponseEntity<ApiResponse<String>> checkLoginId(@AuthenticationPrincipal CustomUserDetails customUserDetails, @ModelAttribute ValidateLoginIdRequest request) {
        adminService.validateLoginId(customUserDetails, request.getLoginId());
        return ResponseEntity.ok(ApiResponse.ok("사용 가능한 아이디입니다."));
    }

    @GetMapping("/search")
    @Operation(summary = "운영자 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<AdminSearchResponse>>> getAdminList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @ModelAttribute AdminSearchRequest request,
            @PageableDefault(size = 10, sort = "createdAt", direction = DESC) Pageable pageable){
        SearchResult<AdminSearchResponse> responseList = adminService.getAdminList(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(responseList));
    }

    @PutMapping("/delete")
    @Operation(summary = "선택된 운영자들 삭제 상태 처리 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<DelAdminResponse>>> deleteAdmins(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                     @RequestBody List<Long> adminIds){
        List<DelAdminResponse> response = adminService.deleteAdmins(customUserDetails, adminIds);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/login-enabled")
    @Operation(summary = "선택된 운영자들 로그인 제한 처리 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<EnableLoginResponse>>> blockAdmins(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                              @RequestBody List<Long> adminIds){
        List<EnableLoginResponse> response = adminService.blockAdmins(customUserDetails, adminIds);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/search/log")
    @Operation(summary = "개인정보 접속 기록 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<UnifiedLogGroupResponse>> getLogList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @ModelAttribute AdminLogSearchRequest request,
            @PageableDefault(size = 10, sort = "createdAt", direction = DESC) Pageable pageable){
        UnifiedLogGroupResponse responseList = adminService.getLogList(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(responseList));
    }

}
