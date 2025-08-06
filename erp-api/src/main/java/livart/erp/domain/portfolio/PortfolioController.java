package livart.erp.domain.portfolio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.Auth.CustomUserDetails;
import livart.common.dto.response.ApiResponse;
import livart.common.mapper.SearchResult;
import livart.erp.domain.order.OrderService;
import livart.erp.domain.order.dto.request.OrderSearchRequest;
import livart.erp.domain.order.dto.response.OrderInfoResponse;
import livart.erp.domain.portfolio.dto.request.DisplayUpdateRequest;
import livart.erp.domain.portfolio.dto.request.PfRegisterRequest;
import livart.erp.domain.portfolio.dto.request.PfSearchRequest;
import livart.erp.domain.portfolio.dto.request.PfStatusRequest;
import livart.erp.domain.portfolio.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "납품 사례 및 세금 계산서 관련 API",description = "✅ 개발 완료")
@RequestMapping("api/erp/sub-order")
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final OrderService orderService;


    @PostMapping("/order/search")
    @Operation(summary = "✅ 거래 정보 불러오기 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<OrderInfoResponse>>> getOrderInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody OrderSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "orderDate", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable)
    {
        SearchResult<OrderInfoResponse> response = orderService.getOrderInfo(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/portfolio")
    @Operation(summary = "✅ 납품 사례 등록 API, 토큰 O")
    public ResponseEntity<ApiResponse<PfResponse>> registerPortfolio(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody PfRegisterRequest request){

        PfResponse response = portfolioService.registerPf(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/portfolio/{portfolioId}")
    @Operation(summary = "✅ 납품 사례 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<PfResponse>> getPortfolio(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long portfolioId){

        PfResponse response = portfolioService.getPf(customUserDetails, portfolioId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/portfolio/{portfolioId}")
    @Operation(summary = "✅ 납품 사례 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<PfResponse>> updatePortfolio(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long portfolioId,
            @RequestBody PfRegisterRequest request){

        PfResponse response = portfolioService.updatePf(customUserDetails, portfolioId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/portfolio/status")
    @Operation(summary = "✅ 납품 사례 리스트 상태 변경(삭제 OR 등록) 처리 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<PfStatusResponse>>> UpdateStatusPf(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                              @RequestBody PfStatusRequest request){
        List<PfStatusResponse> responses = portfolioService.updateStatusPf(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @PostMapping("/portfolio/search")
    @Operation(summary = "✅ 납품 목록 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<PfSearchResponse>>> searchPf(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody PfSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "createdAt",direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable){

        SearchResult<PfSearchResponse> response = portfolioService.searchPf(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/portfolio/display")
    @Operation(summary = "✅ 납품 사례 진열 관리 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<PfDisplayResponse>>> getDisplayPf(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody PfSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "createdAt",direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable) {

        SearchResult<PfDisplayResponse> response = portfolioService.getDisplayPf(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/portfolio/display")
    @Operation(summary = "✅ 납품 사례 진열 관리 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<PfDisplayUpdateResponse>>> updateDisplayPf(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody List<DisplayUpdateRequest> request) {

        List<PfDisplayUpdateResponse> response = portfolioService.updateDisplayPf(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/portfolio/pin/{portfolioId}")
    @Operation(summary = "✅ 납품 사례 상단 고정 토글 API, 토큰 O")
    public ResponseEntity<ApiResponse<PfDisplayUpdateResponse>> togglePinned(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long portfolioId) {

        PfDisplayUpdateResponse response = portfolioService.togglePinned(customUserDetails, portfolioId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
