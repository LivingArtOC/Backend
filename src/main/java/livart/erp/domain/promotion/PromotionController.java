package livart.erp.domain.promotion;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.Auth.CustomUserDetails;
import livart.common.dto.response.ApiResponse;
import livart.erp.domain.promotion.dto.request.CatalogRequest;
import livart.erp.domain.promotion.dto.request.CouponSettingRequest;
import livart.erp.domain.promotion.dto.response.CatalogResponse;
import livart.erp.domain.promotion.dto.response.CouponSettingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/erp/promotion")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "프로모션 관련 API")
public class PromotionController {
    private final PromotionService promotionService;

    @PutMapping("/catalog")
    @Operation(summary = "카탈로그 등록 API, 토큰 O", description = "없으면 저장 있으면 수정")
    public ResponseEntity<ApiResponse<List<CatalogResponse>>> saveCatalog(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                          @RequestBody CatalogRequest request){
        List<CatalogResponse> responses = promotionService.saveCatalog(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(responses));
    }

    @GetMapping("/catalog")
    @Operation(summary = "카탈로그 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<CatalogResponse>>> getCatalog(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<CatalogResponse> responses = promotionService.getCatalog(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @PutMapping("/coupon/default")
    @Operation(summary = "쿠폰 기본 설정 등록 API, 토큰 O", description = "없으면 저장 있으면 수정")
    public ResponseEntity<ApiResponse<CouponSettingResponse>> saveCouponSetting(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                          @RequestBody CouponSettingRequest request){
        CouponSettingResponse response = promotionService.saveCouponSetting(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/coupon/default")
    @Operation(summary = "쿠폰 기본 설정 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<CouponSettingResponse>> getCouponSetting(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        CouponSettingResponse response = promotionService.getCouponSetting(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
