package livart.erp.domain.promotion;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.Auth.CustomUserDetails;
import livart.common.dto.enums.coupon.IssuedStatus;
import livart.common.dto.request.CouponRegisterRequest;
import livart.common.dto.response.ApiResponse;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.mapper.SearchResult;
import livart.erp.domain.promotion.dto.request.*;
import livart.erp.domain.promotion.dto.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/erp/promotion")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "프로모션 관련 API", description = "✅✅ 개발 완료")
public class PromotionController {
    private final PromotionService promotionService;

    @PutMapping("/catalog")
    @Operation(summary = "✅ 카탈로그 저장 & 수정 API, 토큰 O", description = "없으면 저장 있으면 수정")
    public ResponseEntity<ApiResponse<List<CatalogResponse>>> saveCatalog(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                          @RequestBody CatalogRequest request){
        List<CatalogResponse> responses = promotionService.saveCatalog(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @GetMapping("/catalog")
    @Operation(summary = "✅ 카탈로그 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<CatalogResponse>>> getCatalog(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<CatalogResponse> responses = promotionService.getCatalog(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @PutMapping("/coupon/default")
    @Operation(summary = "✅ 쿠폰 기본 설정 등록 API, 토큰 O", description = "없으면 저장 있으면 수정")
    public ResponseEntity<ApiResponse<CouponSettingResponse>> saveCouponSetting(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                          @RequestBody CouponSettingRequest request){
        CouponSettingResponse response = promotionService.saveCouponSetting(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/coupon/default")
    @Operation(summary = "✅ 쿠폰 기본 설정 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<CouponSettingResponse>> getCouponSetting(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        CouponSettingResponse response = promotionService.getCouponSetting(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/coupon")
    @Operation(summary = "✅ 쿠폰 등록 API, 토큰 O")
    public ResponseEntity<ApiResponse<CouponRegisterResponse>> registerCoupon(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                              @RequestBody CouponRegisterRequest request){
        CouponRegisterResponse response = promotionService.saveCoupon(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/coupon/{couponId}")
    @Operation(summary = "✅ 쿠폰 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<CouponRegisterResponse>> getCoupon(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                              @PathVariable Long couponId){
        CouponRegisterResponse response = promotionService.getCoupon(customUserDetails, couponId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/coupon/{couponId}")
    @Operation(summary = "✅ 쿠폰 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<CouponRegisterResponse>> updateCoupon(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                         @RequestBody CouponRegisterRequest request,
                                                                         @PathVariable Long couponId){
        CouponRegisterResponse response = promotionService.updateCoupon(customUserDetails,request, couponId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/coupon/auto")
    @Operation(summary = "✅ 쿠폰 자동 지급 설정 조회 API, 토큰 O")
    private ResponseEntity<ApiResponse<List<CouponAutoResponse>>> getCouponAutoInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<CouponAutoResponse> responses = promotionService.getCouponAutoInfo(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @PutMapping("/coupon/auto")
    @Operation(summary = "✅ 쿠폰 자동 지급 설정 저장 및 수정 API, 토큰 O")
    private ResponseEntity<ApiResponse<List<CouponAutoResponse>>> saveCouponAutoInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                                     @RequestBody List<CouponAutoRequest> request){
        List<CouponAutoResponse> responses = promotionService.saveCouponAutoInfo(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @PostMapping("/coupon/issued")
    @Operation(summary = "✅ 자동 지급 쿠폰 지정 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<AutoCouponList>>> getIssuedCouponList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody IssuedCouponResearchRequest request){
        List<AutoCouponList> couponLists = promotionService.getIssuedCouponList(customUserDetails,request);
        return ResponseEntity.ok(ApiResponse.ok(couponLists));
    }

    @PostMapping("/coupon/search")
    @Operation(summary = "✅ 쿠폰 목록 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<CouponSearchResponse>>> getCouponList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CouponSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = DESC)
            @Parameter(hidden = true) Pageable pageable){
        SearchResult<CouponSearchResponse> result = promotionService.getCouponList(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PutMapping("/coupon/status/{status}")
    @Operation(summary = "✅ 선택된 쿠폰들 발급 상태 변경 API, 토큰 O")
    public  ResponseEntity<ApiResponse<List<CouponSearchResponse>>> updateCouponStatus(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                                       @RequestBody List<Long> couponIdList,
                                                                                       @PathVariable String status){
        IssuedStatus issuedStatus = parseIssuedStatus(status);
        List<CouponSearchResponse> result = promotionService.updateCouponStatus(customUserDetails, couponIdList, issuedStatus);
        return ResponseEntity.ok(ApiResponse.ok(result));

    }

    private IssuedStatus parseIssuedStatus(String status) {
        try {
            return IssuedStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_COUPON_ISSUED_STATUS);
        }
    }


}
