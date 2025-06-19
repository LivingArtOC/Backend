package livart.erp.domain.product.category;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.Auth.CustomUserDetails;
import livart.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "제품 카테고리 설정 관련 API", description = "✅✅ 개발 완료")
@RequestMapping("api/erp")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/category")
    @Operation(summary = "✅ 카테고리 저장 API, 사용 X")
    public ResponseEntity<ApiResponse<List<CategoryTreeResponse>>> categoryRegister(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                                    @RequestBody List<CategoryRequest> requests){
        List<CategoryTreeResponse> response = categoryService.categoryRegister(customUserDetails, requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/category/select")
    @Operation(summary = "✅ 카테고리 목록 조회 API")
    public ResponseEntity<ApiResponse<List<CategoryTreeResponse>>> categoryList(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<CategoryTreeResponse> responses = categoryService.getCategoryList(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }
}
