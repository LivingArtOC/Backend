package livart.erp.domain.support.quotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.Auth.CustomUserDetails;
import livart.common.domain.support.quotation.entity.Quotation;
import livart.common.dto.response.ApiResponse;
import livart.common.mapper.SearchResult;
import livart.erp.domain.support.quotation.dto.request.QuotationRequest;
import livart.erp.domain.support.quotation.dto.request.QuotationSearchRequest;
import livart.erp.domain.support.quotation.dto.response.DefaultInfoResponse;
import livart.erp.domain.support.quotation.dto.response.QuotationAllResponse;
import livart.erp.domain.support.quotation.dto.response.QuotationResponse;
import livart.erp.domain.support.quotation.dto.response.QuotationSearchResponse;
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
@Tag(name = "견적서 관련 API", description = "✅ 개발 완료")
@RequestMapping("api/erp/quotation")
public class QuotationController {
    private final QuotationService quotationService;

    @GetMapping("/all/{estimateId}")
    @Operation(summary = "✅ 견적서 등록을 위한 견적 문의의 모든 정보 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<QuotationAllResponse>> getAllInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                        @PathVariable Long estimateId){
        QuotationAllResponse response = quotationService.getAllInfo(customUserDetails, estimateId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/default-info")
    @Operation(summary = "✅ 견적서 직접 등록 시 공급자에 들어갈 정보 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<DefaultInfoResponse>> getDefaultInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        DefaultInfoResponse response = quotationService.getDefault(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/register")
    @Operation(summary = "✅ 견적서 등록 API, 토큰 O")
    public ResponseEntity<ApiResponse<QuotationResponse>> registerQuotation(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                            @RequestBody QuotationRequest request){
        QuotationResponse response = quotationService.registerQuotation(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/{quotationId}")
    @Operation(summary = "✅ 견적서 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<QuotationResponse>> getQuotation(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                            @PathVariable Long quotationId){
        QuotationResponse response = quotationService.getQuotation(customUserDetails, quotationId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{quotationId}")
    @Operation(summary = "✅ 견적서 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<QuotationResponse>> updateQuotation(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                       @PathVariable Long quotationId,
                                                                          @RequestBody QuotationRequest request){
        QuotationResponse response = quotationService.updateQuotation(customUserDetails, quotationId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/search")
    @Operation(summary = "✅ 견적서 목록 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<QuotationSearchResponse>>> searchQuotation(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody QuotationSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable
            ){

        SearchResult<QuotationSearchResponse> response = quotationService.searchQuotation(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("")
    @Operation(summary = "✅ 견적서 삭제 API, 토큰 O")
    public ResponseEntity<ApiResponse<String>> deleteQuotation(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                               @RequestBody List<Long> idList){
        quotationService.deleteQuotation(customUserDetails, idList);
        return ResponseEntity.ok(ApiResponse.ok("성공적으로 삭제되었습니다"));
    }
}
