package livart.erp.domain.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import livart.common.Auth.CustomUserDetails;
import livart.common.dto.response.ApiResponse;
import livart.common.mapper.SearchResult;
import livart.erp.domain.member.dto.request.MemberRequest;
import livart.erp.domain.member.dto.request.MemberSearchRequest;
import livart.erp.domain.member.dto.request.MileageUpdateRequest;
import livart.erp.domain.member.dto.response.MemberResponse;
import livart.erp.domain.member.dto.response.MemberSearchResponse;
import livart.erp.domain.member.dto.response.MileageUpdateResponse;
import livart.erp.domain.member.dto.response.StatusResponse;
import livart.shop.security.dto.response.SignupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "회원 탭 관련 API")
@RequestMapping("api/erp/member")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/upload-excel")
    @Operation(summary = "회원 엑셀 일괄 등록 API. 토큰 O")
    public ResponseEntity<ApiResponse<String>> uploadMemberExcel(@RequestPart MultipartFile file) {
        memberService.processUserExcel(file);
        return ResponseEntity.ok(ApiResponse.ok("업로드 및 저장 완료"));
    }

    @PostMapping("/register")
    @Operation(summary = "회원 등록(관리자) API, 토큰 O")
    public ResponseEntity<ApiResponse<SignupResponse>> registerMember(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                      @RequestBody MemberRequest request){
        SignupResponse response = memberService.registerMember(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/search")
    @Operation(summary = "회원 목록 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<MemberSearchResponse>>> searchMember(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @ModelAttribute MemberSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){

        SearchResult<MemberSearchResponse> response = memberService.searchMember(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{status}")
    @Operation(summary = "선택 회원들 상태 변경 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<StatusResponse>>> updateStatus(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody List<Long> idList, @PathVariable String status, HttpServletRequest request){

        List<StatusResponse> responses = memberService.updateStatus(customUserDetails, idList, status, request);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @PutMapping("/mileage")
    @Operation(summary = "마일리지 부여 및 차감 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<MileageUpdateResponse>>> updateMileage(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody MileageUpdateRequest request){

        List<MileageUpdateResponse> responses = memberService.updateMileage(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }


}
