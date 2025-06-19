package livart.erp.domain.mileage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.Auth.CustomUserDetails;
import livart.common.dto.response.ApiResponse;
import livart.common.mapper.SearchResult;
import livart.common.dto.request.MileageDefaultDto;
import livart.erp.domain.mileage.dto.request.MileageSearchRequest;
import livart.common.dto.request.MileageUsePayDto;
import livart.erp.domain.mileage.dto.response.MileageSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "마일리지 관련 설정 API", description = "✅✅ 개발 완료")
@RequestMapping("/api/erp/mileage")
public class MileageController {
    private final MileageService mileageService;

    @PutMapping("/default")
    @Operation(summary = "✅ 마일리지 기본 설정 저장 & 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<MileageDefaultDto>> updateDefault(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                        @RequestBody MileageDefaultDto request){
        MileageDefaultDto response = mileageService.updateDefault(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/default")
    @Operation(summary = "✅ 마일리지 기본 설정 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<MileageDefaultDto>> getDefault(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        MileageDefaultDto response = mileageService.getDefault(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/use-pay")
    @Operation(summary = "✅ 마일리지 사용 및 지급 설정 저장 & 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<MileageUsePayDto>> updateUsePay(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                       @RequestBody MileageUsePayDto request){
        MileageUsePayDto response = mileageService.updateUsePay(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/use-pay")
    @Operation(summary = "✅ 마일리지 사용 및 지급 설정 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<MileageUsePayDto>> getUsePay(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        MileageUsePayDto response = mileageService.getUsePay(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/search")
    @Operation(summary = "✅ 마일리지 사용/지급 내역 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<MileageSearchResponse>>> searchMileageLog(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody MileageSearchRequest request,
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable){

        SearchResult<MileageSearchResponse> response = mileageService.searchMileageLog(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
