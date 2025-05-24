package livart.erp.domain.product.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import livart.common.Auth.CustomUserDetails;
import livart.common.domain.product.entity.Product;
import livart.common.domain.product.repository.ProductRepository;
import livart.common.dto.response.ApiResponse;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.mapper.SearchResult;
import livart.erp.domain.product.excel.ExcelService;
import livart.erp.domain.product.product.dto.request.*;
import livart.erp.domain.product.product.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "제품 관련 설정 관련 API", description = "✅ 개발 완료")
@RequestMapping("api/erp/product")
public class ProductController {

    private final ProductService productService;
    private final ExcelService excelService;
    private final ProductRepository productRepository;

    @PostMapping("")
    @Operation(summary = "✅ 제품 신규 등록 API, 토큰 O")
    public ResponseEntity<ApiResponse<ProductRegisterResponse>> registerProduct(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                                @RequestBody ProductRegisterRequest request){
        ProductRegisterResponse response = productService.registerProduct(customUserDetails, request);
        return  ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/{productId}")
    @Operation(summary = "✅ 특정 제품 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<ProductRegisterResponse>> getProduct(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                           @PathVariable Long productId){
        ProductRegisterResponse response = productService.getProduct(customUserDetails, productId);
        return  ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{productId}")
    @Operation(summary = "✅ 특정 제품 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<ProductRegisterResponse>> updateProduct(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                              @PathVariable Long productId,
                                                                              @RequestBody ProductRegisterRequest request){
        ProductRegisterResponse response = productService.updateProduct(customUserDetails, productId, request);
        return  ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/search")
    @Operation(summary = "✅ 일반 상품 & 삭제 상품 목록 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<ProductSearchResponse>>> getProductSearchList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody ProductSearchRequest request,
            @PageableDefault(size = 10, sort = "createdAt", direction = DESC)
            @Parameter(hidden = true) Pageable pageable){
        SearchResult<ProductSearchResponse> response = productService.getProductSearchList(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/out-stock")
    @Operation(summary = "✅ 상품들 품절 처리 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<ProductStockResponse>>> updateOutStock(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                                  @RequestBody IdListRequest request){
        List<ProductStockResponse> response = productService.updateOutStock(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/in-stock")
    @Operation(summary = "✅ 상품들 정상(재입고) 처리 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<ProductStockResponse>>> updateInStock(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                                  @RequestBody IdListRequest request){
        List<ProductStockResponse> response = productService.updateInStock(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/soft-delete")
    @Operation(summary = "✅ 상품들 삭제 처리(소프트 삭제) API, 토큰 O")
    public ResponseEntity<ApiResponse<List<ProductDeactiveResponse>>> updateDeactivate(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                                              @RequestBody IdListRequest request){
        List<ProductDeactiveResponse> response = productService.updateDeactivate(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/restore")
    @Operation(summary = "✅ 상품들 복구 처리 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<ProductDeactiveResponse>>> updateRestore(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                                       @RequestBody IdListRequest request){
        List<ProductDeactiveResponse> response = productService.updateRestore(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }


    @PostMapping("/manage")
    @Operation(summary = "✅ 상품 가격 관리 & 품절 관리 페이지 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<ProductSearchResponse>>> getProductManageList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody ProductBatchRequest request,
            @PageableDefault(size = 10, sort = "createdAt", direction = DESC)
            @Parameter(hidden = true) Pageable pageable){

        SearchResult<ProductSearchResponse> response = productService.getProductManageList(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/change-price")
    @Operation(summary = "✅ 상품들 가격 일괄 적용 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<ProductDeactiveResponse>>> updatePrice(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                                  @RequestBody PriceBatchChangeRequest request){
        List<ProductDeactiveResponse> response = productService.updatePrice(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/display/{categoryId}")
    @Operation(summary = "✅ 상품들 카테고리별 진열 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<ProductDisplayResponse>>> getDisplay(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long categoryId,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = DESC)
            @Parameter(hidden = true) Pageable pageable){
        SearchResult<ProductDisplayResponse> responses = productService.getDisplay(customUserDetails, categoryId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @PutMapping("/display/pin/{productId}")
    @Operation(summary = "✅ 특정 상품 고정 on/off API, 토큰 O")
    public ResponseEntity<ApiResponse<ProductDisplayResponse>> togglePinned(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                            @PathVariable Long productId){
        ProductDisplayResponse response = productService.togglePinned(customUserDetails, productId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/display/{categoryId}")
    @Operation(summary = "✅ 상품들 카테고리별 진열 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<ProductDisplayResponse>>> updateDisplay(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                                        @PathVariable Long categoryId,
                                                                                        @RequestBody List<ProductOrderUpdateRequest> requests){
        List<ProductDisplayResponse> responses = productService.updateDisplay(customUserDetails, categoryId, requests);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @PostMapping("/add/search")
    @Operation(summary = "✅ 상품 추가 목적의 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<ProductAddSearchResponse>>> addProductSearch(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody ProductAddRequest request,
            @PageableDefault(page = 0, size = 100, sort = "createdAt", direction = DESC)
            @Parameter(hidden = true) Pageable pageable){

        SearchResult<ProductAddSearchResponse> response = productService.addProductSearch(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/add/detail/{productId}")
    @Operation(summary = "✅ 상품 목록의 추가 버튼 API, 토큰 O")
    public ResponseEntity<ApiResponse<ProductAddDto>> getProductDetail(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long productId){

        ProductAddDto response = productService.getProductDetail(customUserDetails, productId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }


    @PostMapping("/excel/download")
    @Operation(summary = "상품 목록 엑셀 다운 API, 토큰 O")
    public ResponseEntity<ApiResponse<String>> excelDownload(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                             @RequestBody IdListRequest request,
                                                             HttpServletResponse response) throws IOException {
        List<Product> products = productRepository.findAllById(request.getProductIdList());

        if (products.isEmpty()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        Workbook workbook = excelService.generateProductExcel(products);

        String filename = "products_" + LocalDate.now() + ".xlsx";
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename);

        workbook.write(response.getOutputStream());
        workbook.close();

        return ResponseEntity.ok(ApiResponse.ok("엑셀 생성 성공"));
    }
}
