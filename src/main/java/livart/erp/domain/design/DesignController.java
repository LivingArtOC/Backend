package livart.erp.domain.design;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.Auth.CustomUserDetails;
import livart.common.dto.response.ApiResponse;
import livart.common.mapper.SearchResult;
import livart.erp.domain.design.dto.request.*;
import livart.erp.domain.design.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "관리 정책 설정 관련 API")
@RequestMapping("api/erp/design")
public class DesignController {
    private final DesignService designService;

    @PutMapping("/brand")
    @Operation(summary = "브랜드 소개 저장 API, 토큰 O", description = "없으면 저장되고 이미 존재하면 수정되는 구조의 API")
    public ResponseEntity<ApiResponse<BrandResponse>> saveBrandInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                    @RequestBody BrandRequest request) {
        BrandResponse response = designService.saveBrandInfo(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/brand")
    @Operation(summary = "브랜드 소개 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<BrandResponse>> getBrandInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        BrandResponse response = designService.getBrandInfo(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/product-banner")
    @Operation(summary = "제품 페이지 배너 저장 API, 토큰 O", description = "없으면 저장되고 이미 존재하면 수정되는 구조의 API")
    public ResponseEntity<ApiResponse<List<ProductBannerResponse>>> saveProductBannerInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                            @RequestBody List<ImageListDto> request) {
        List<ProductBannerResponse> response = designService.saveProductBannerInfo(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/product-banner")
    @Operation(summary = "제품 페이지 배너 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<ProductBannerResponse>>> getProductBannerInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<ProductBannerResponse> response = designService.getProductBannerInfo(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/popup")
    @Operation(summary = "팝업창 등록 API, 토큰 O")
    public ResponseEntity<ApiResponse<PopupResponse>> savePopupInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                    @RequestBody PopupRegisterRequest request) {
        PopupResponse response = designService.savePopupInfo(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/popup/{popupId}")
    @Operation(summary = "팝업창 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<PopupResponse>> getPopupInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                   @PathVariable Long popupId) {
        PopupResponse response = designService.getPopupInfo(customUserDetails,popupId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/popup/{popupId}")
    @Operation(summary = "팝업창 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<PopupResponse>> updatePopupInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                      @RequestBody PopupRegisterRequest request,
                                                                      @PathVariable Long popupId) {
        PopupResponse response = designService.updatePopupInfo(customUserDetails, request, popupId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/popup/manage")
    @Operation(summary = "팝업창 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<PopupResponse>>> searchPopupList(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                                    @ModelAttribute PopupSearchRequest request){
        SearchResult<PopupResponse> response = designService.searchPopupList(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/popup/del")
    @Operation(summary = "팝업 삭제 처리 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<PopupDeleteResponse>>> deletePopupList(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                                  @RequestBody List<Long> popupIdList){
        List<PopupDeleteResponse> responseList = designService.deletePopupList(customUserDetails, popupIdList);
        return ResponseEntity.ok(ApiResponse.ok(responseList));
    }

    @PutMapping("/interior")
    @Operation(summary = "전시장 안내 저장 API, 토큰 O", description = "없으면 저장되고 이미 존재하면 수정되는 구조의 API")
    public ResponseEntity<ApiResponse<InteriorInfoResponse>> saveInteriorInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                           @RequestBody InteriorInfoRequest request) {
        InteriorInfoResponse response = designService.saveInteriorInfo(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/interior")
    @Operation(summary = "전시장 안내 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<InteriorInfoResponse>> getInteriorInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        InteriorInfoResponse response = designService.getInteriorInfo(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/main-banner")
    @Operation(summary = "메인 페이지 배너 저장 API, 토큰 O", description = "없으면 저장되고 이미 존재하면 수정되는 구조의 API")
    public ResponseEntity<ApiResponse<List<MainBannerResponse>>> saveMainBannerInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                                          @RequestBody List<ImageListDto> request) {
        List<MainBannerResponse> response = designService.saveMainBannerInfo(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/main-banner")
    @Operation(summary = "메인 페이지 배너 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<MainBannerResponse>>> getmainBannerInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<MainBannerResponse> response = designService.getMainBannerInfo(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

}