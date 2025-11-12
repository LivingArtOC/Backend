package livart.erp.domain.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import livart.common.Auth.CustomUserDetails;
import livart.common.dto.enums.ActionType;
import livart.common.dto.response.ApiResponse;
import livart.common.mapper.SearchResult;
import livart.common.service.GlobalService;
import livart.erp.domain.member.dto.request.*;
import livart.erp.domain.member.dto.response.*;
import livart.erp.domain.order.OrderService;
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
@Tag(name = "회원 탭 관련 API", description = "✅✅ 개발 완료")
@RequestMapping("api/erp/member")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/upload-excel")
    @Operation(summary = "회원 엑셀 일괄 등록 API. 토큰 O")
    public ResponseEntity<ApiResponse<String>> uploadMemberExcel(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart MultipartFile file) {
        memberService.processUserExcel(customUserDetails, file);
        return ResponseEntity.ok(ApiResponse.ok("업로드 및 저장 완료"));
    }

    @PostMapping("/register")
    @Operation(summary = "✅ 회원 등록(관리자) API, 토큰 O")
    public ResponseEntity<ApiResponse<MemberResponse>> registerMember(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                      @RequestBody MemberRequest request){
        MemberResponse response = memberService.registerMember(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PostMapping("/search")
    @Operation(summary = "✅ 회원 목록 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<MemberSearchResponse>>> searchMember(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody MemberSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable){

        SearchResult<MemberSearchResponse> response = memberService.searchMember(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }



    @PutMapping("/status/{status}")
    @Operation(summary = "✅ 선택 회원들 상태 변경 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<StatusResponse>>> updateStatus(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody List<Long> idList, @PathVariable String status, HttpServletRequest request){

        List<StatusResponse> responses = memberService.updateStatus(customUserDetails, idList, status, request);

        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @PutMapping("/mileage")
    @Operation(summary = "✅ 마일리지 부여 및 차감 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<MileageUpdateResponse>>> updateMileage(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody MileageUpdateRequest request,
            HttpServletRequest httpServletRequest){

        List<MileageUpdateResponse> responses = memberService.updateMileage(customUserDetails, request, httpServletRequest);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "✅ 회원 세부 정보 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                     @PathVariable Long userId,
                                                                     HttpServletRequest request){

        MemberResponse response = memberService.getMemberInfo(customUserDetails, userId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/memo")
    @Operation(summary = "✅ 회원 세부 정보 페이지에서 관리자 메모 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<String>> updateMemo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                             @RequestBody MemoUpdateRequest request,
                                                          HttpServletRequest httpServletRequest){

        memberService.updateMemo(customUserDetails, request, httpServletRequest);
        return ResponseEntity.ok(ApiResponse.ok("메모가 수정되었습니다."));
    }

    @GetMapping("/mileage/log/{userId}")
    @Operation(summary = "✅ 마일리지 사용 내역 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<MileageLogResponse>>> getMileageLog(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                         @PathVariable Long userId,
                                                                               HttpServletRequest request){

        List<MileageLogResponse> response = memberService.getMileageLog(customUserDetails, userId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/coupon/{userId}")
    @Operation(summary = "✅ 보유 쿠폰 목록 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<UserCouponResponse>>> getUserCoupon(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                               @PathVariable Long userId,
                                                                               HttpServletRequest request){

        List<UserCouponResponse> response = memberService.getUserCoupon(customUserDetails, userId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/coupon/log/{userId}")
    @Operation(summary = "✅ 쿠폰 사용 내역 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<CouponUseLogResponse>>> getCouponLog(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                               @PathVariable Long userId,
                                                                                HttpServletRequest request){

        List<CouponUseLogResponse> response = memberService.getCouponLog(customUserDetails, userId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/dormant/search")
    @Operation(summary = "✅ 휴면 회원 목록 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<DormantSearchResponse>>> searchDormantMember(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody DormantSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable){

        SearchResult<DormantSearchResponse> response = memberService.searchDormantMember(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/delete/search")
    @Operation(summary = "✅ 탈퇴/삭제 회원 목록 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<DeleteSearchResponse>>> searchDeleteMember(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody DeleteSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "deletedAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable){

        SearchResult<DeleteSearchResponse> response = memberService.searchDeleteMember(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }



}
