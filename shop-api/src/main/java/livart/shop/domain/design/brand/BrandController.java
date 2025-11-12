package livart.shop.domain.design.brand;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import livart.common.dto.response.ApiResponse;
import livart.shop.domain.design.brand.dto.response.BrandViewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shop/brand")
@CrossOrigin("*")
@Tag(name = "브랜드 소개 API")
public class BrandController {

    private final BrandService brandService;

    @GetMapping({"", "/"})
    @Operation(summary = "브랜드 소개 이미지 1개 조회(공개)")
    public ResponseEntity<ApiResponse<BrandViewResponse>> getBrand(HttpServletRequest request) {
        BrandViewResponse body = brandService.get();

        // updatedAt(밀리초) 기반 ETag
        String etag = "\"" + body.getUpdatedAt() + "\"";

        // 클라이언트 ETag와 동일하면 304
        String ifNoneMatch = request.getHeader(HttpHeaders.IF_NONE_MATCH);
        if (etag.equals(ifNoneMatch)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                    .eTag(etag)
                    .cacheControl(CacheControl.maxAge(600, TimeUnit.SECONDS).cachePublic())
                    .build();
        }

        return ResponseEntity.ok()
                .eTag(etag)
                .cacheControl(CacheControl.maxAge(600, TimeUnit.SECONDS).cachePublic())
                .body(ApiResponse.ok(body));
    }
}