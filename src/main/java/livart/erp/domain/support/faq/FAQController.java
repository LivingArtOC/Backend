package livart.erp.domain.support.faq;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.Auth.CustomUserDetails;
import livart.common.dto.response.ApiResponse;
import livart.common.mapper.SearchResult;
import livart.erp.domain.support.faq.dto.request.FAQRegisterRequest;
import livart.erp.domain.support.faq.dto.request.FAQSearchRequest;
import livart.erp.domain.support.faq.dto.request.FAQUpdateRequest;
import livart.erp.domain.support.faq.dto.response.FAQResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "FAQ 관련 API", description = "✅ 개발 완료")
@RequestMapping("api/erp/faq")
public class FAQController {
    private final FAQService faqService;

    @PostMapping("")
    @Operation(summary = "✅ FAQ 등록 API, 토큰 O")
    public ResponseEntity<ApiResponse<FAQResponse>> registerFAQ(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                @RequestBody FAQRegisterRequest request){
        FAQResponse response = faqService.registerFAQ(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/{faqId}")
    @Operation(summary = "✅ FAQ 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<FAQResponse>> getFAQ(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                @PathVariable Long faqId){
        FAQResponse response = faqService.getFAQ(customUserDetails, faqId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{faqId}")
    @Operation(summary = "✅ FAQ 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<FAQResponse>> updateFAQ(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                              @PathVariable Long faqId,
                                                              @RequestBody FAQUpdateRequest request){
        FAQResponse response = faqService.updateFAQ(customUserDetails, faqId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/search")
    @Operation(summary = "✅ FAQ 목록 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<FAQResponse>>> searchFAQ(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody FAQSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "questionAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable){

        SearchResult<FAQResponse> response = faqService.searchFAQ(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }


}
