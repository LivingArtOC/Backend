package livart.erp.domain.support.estimate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.Auth.CustomUserDetails;
import livart.common.dto.response.ApiResponse;
import livart.common.mapper.SearchResult;
import livart.erp.domain.support.estimate.dto.request.EstimateSearchRequest;
import livart.erp.domain.support.estimate.dto.request.EstimateUpdateRequest;
import livart.erp.domain.support.estimate.dto.response.EstimateResponse;
import livart.erp.domain.support.estimate.dto.response.EstimateSearchResponse;
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
@Tag(name = "견적 문의 관련 API", description = "✅ 개발 완료")
@RequestMapping("api/erp")
public class EstimateController {

    private final EstimateService estimateService;

    @PostMapping("/estimate/search")
    @Operation(summary = "✅ 견적 문의 목록 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<EstimateSearchResponse>>> searchEstimateList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody EstimateSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "visitDate", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable){

        SearchResult<EstimateSearchResponse> responses = estimateService.searchEstimateList(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @GetMapping("/estimate/{estimateId}")
    @Operation(summary = "✅ 견적 문의 상세 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<EstimateResponse>> getEstimate(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                     @PathVariable Long estimateId){
        EstimateResponse response = estimateService.getEstimate(customUserDetails, estimateId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/estimate/{estimateId}")
    @Operation(summary = "✅ 견적 문의 상세 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<EstimateResponse>> updateEstimate(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                        @PathVariable Long estimateId,
                                                                        @RequestBody EstimateUpdateRequest request){
        EstimateResponse response = estimateService.updateEstimate(customUserDetails, estimateId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

}
