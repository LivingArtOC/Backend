package livart.shop.domain.design.faq;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.dto.response.ApiResponse;
import livart.shop.domain.design.faq.dto.response.FaqResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shop/faq")
@CrossOrigin("*")
@Tag(name = "FAQ API")
public class FaqController {

    private final FaqService faqService;

    @GetMapping()
    @Operation(summary = "FAQ 목록 조회(공개)", description = "type 미전달 또는 ALL → 전체, 그 외는 ENUM 필터. 결과 0건이면 404.")
    public ResponseEntity<ApiResponse<List<FaqResponse>>> getFaqs(
            @RequestParam(required = false) String type
    ) {
        List<FaqResponse> body = faqService.getFaqs(type);
        return ResponseEntity.ok(ApiResponse.ok(body));
    }
}