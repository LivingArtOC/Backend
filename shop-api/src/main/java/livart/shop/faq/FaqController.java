package livart.shop.faq;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.dto.response.ApiResponse; // 프로젝트 공통 응답 사용
import livart.shop.faq.dto.request.FaqSearchRequest;
import livart.shop.faq.dto.response.FaqPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shop/faq")
@Tag(name = "FAQ")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://staging.artliving.store",
        "https://artliving.store"
})
public class FaqController {

    private final FaqService service;

    @GetMapping
    @Operation(summary = "FAQ 목록 조회")
    public ResponseEntity<ApiResponse<FaqPageResponse>> getFaqs(
            @Valid @ModelAttribute FaqSearchRequest req,
            @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch
    ) {
        String etag = service.generateEtag(req.categoryOrAll());
        if (etag.equals(ifNoneMatch)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(etag).build();
        }
        FaqPageResponse body = service.list(req);
        return ResponseEntity.ok().eTag(etag).body(ApiResponse.ok(body));
    }
}