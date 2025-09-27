package livart.shop.brand;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shop/brand")
@Tag(name = "브랜드 소개")
@CrossOrigin(origins = {
        "http://localhost:3000",
        "https://staging.artliving.store",
        "https://artliving.store"
})
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    @Operation(summary = "브랜드 소개 이미지 1개 조회(공개)")
    public ResponseEntity<ApiResponse<BrandViewResponse>> getBrand() {
        BrandViewResponse body = brandService.get();
        String etag = (body.updatedAt() == null) ? "\"0\"" : "\"" + body.updatedAt().hashCode() + "\"";
        return ResponseEntity.ok().eTag(etag).body(ApiResponse.ok(body));
    }
}
