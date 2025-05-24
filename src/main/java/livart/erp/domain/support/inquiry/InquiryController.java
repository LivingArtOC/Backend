package livart.erp.domain.support.inquiry;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.Auth.CustomUserDetails;
import livart.common.dto.response.ApiResponse;
import livart.common.mapper.SearchResult;
import livart.erp.domain.support.inquiry.dto.request.AnswerRequest;
import livart.erp.domain.support.inquiry.dto.request.InquirySearchRequest;
import livart.erp.domain.support.inquiry.dto.response.InquiryResponse;
import livart.erp.domain.support.inquiry.dto.response.InquirySearchResponse;
import livart.erp.domain.support.inquiry.dto.response.InquiryStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "고객문의 관련 API", description = "✅ 개발 완료")
@RequestMapping("/api/erp/inq")
public class InquiryController {
    private final InquiryService inquiryService;

    @PostMapping("/search")
    @Operation(summary = "✅ 고객 문의 검색 목록 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<InquirySearchResponse>>> searchInquiry(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody InquirySearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "questionAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable){

        SearchResult<InquirySearchResponse> response = inquiryService.searchInquiry(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{inquiryId}")
    @Operation(summary = "✅ 고객 문의 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<InquiryResponse>> getInquiry(
            @AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long inquiryId){
        InquiryResponse response = inquiryService.getInquiry(customUserDetails, inquiryId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{inquiryId}")
    @Operation(summary = "✅ 고객 문의 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<InquiryResponse>> updateInquiry(
            @AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long inquiryId, @RequestBody AnswerRequest request){
        InquiryResponse response = inquiryService.updateInquiry(customUserDetails, inquiryId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/status/{status}")
    @Operation(summary = "✅ 고객 문의 상태 일괄 변경 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<InquiryStatusResponse>>> updateStatus(
            @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody List<Long> idList, @PathVariable String status){
        List<InquiryStatusResponse> response = inquiryService.updateStatus(customUserDetails, idList, status);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
