package livart.erp.domain.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.Auth.CustomUserDetails;
import livart.common.dto.response.ApiResponse;
import livart.common.mapper.SearchResult;
import livart.erp.domain.order.dto.request.*;
import livart.erp.domain.order.dto.response.*;

import livart.erp.domain.support.quotation.dto.response.DefaultInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "주문 관련 API", description = "✅ 개발 완료")
@RequestMapping("api/erp/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/all")
    @Operation(summary = "✅ 주문통합 리스트 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<OrderAllResponse>>> getAllOrders(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody OrderSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "orderDate", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable)
    {
        SearchResult<OrderAllResponse> response = orderService.getAllOrders(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/{status}")
    @Operation(summary = "✅ 입금대기, 결제완료, 출고 대기중, 배송완료, 구매확정, 결제중단/실패 각 주문 리스트 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<OrderIndResponse>>> getIndOrderList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody OrderSearchRequest request,
            @PathVariable String status,
            @PageableDefault(page = 0, size = 10, sort = "orderDate", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable)
    {
        SearchResult<OrderIndResponse> response = orderService.getIndOrderList(customUserDetails, request, status, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/pending")
    @Operation(summary = "✅ 입금대기 리스트에서 주문 상태 변경 API, 토큰 O")
    public ResponseEntity<ApiResponse<String>> updatePending(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                             @RequestBody UpdateStatusRequest request){
        orderService.updatePending(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok("상태가 변경되었습니다."));
    }

    @PutMapping("/paid")
    @Operation(summary = "✅ 결제완료, 상품준비중, 배송완료, 구매확정 리스트에서 주문 상태 변경 API, 토큰 O")
    public ResponseEntity<ApiResponse<String>> updatePaid(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                             @RequestBody UpdateStatusRequest request){
        orderService.updatePaid(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok("상태가 변경되었습니다."));
    }

    @PutMapping("/fail")
    @Operation(summary = "✅ 결제 중단/실패 혹은 취소 리스트에서 일괄 삭제 API, 토큰 O")
    public ResponseEntity<ApiResponse<String>> deleteItem(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                          @RequestBody List<Long> idList){
        orderService.deleteItem(customUserDetails, idList);
        return ResponseEntity.ok(ApiResponse.ok("상태가 변경되었습니다."));
    }

    @PostMapping("/claim/search")
    @Operation(summary = "✅ 취소/교환/반품/환불 요청 리스트 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<ClaimSearchResponse>>> getClaimList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody ClaimSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable)
    {
        SearchResult<ClaimSearchResponse> response = orderService.getClaimList(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/claim/status")
    @Operation(summary = "✅ 취소/교환/반품/환불 요청 상태 변경 API, 토큰 O")
    public ResponseEntity<ApiResponse<ClaimChangeStatusResponse>> changeStatus(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                               @RequestBody ClaimChangeStatusRequest request){
        ClaimChangeStatusResponse response = orderService.changeStatus(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/as/search")
    @Operation(summary = "✅ A/S 요청 리스트 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<AsSearchResponse>>> getAsList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody AsSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable)
    {
        SearchResult<AsSearchResponse> response = orderService.getAsList(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/as/image/{asId}")
    @Operation(summary = "✅ 특정 A/S 요청의 image/video 목록 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<AsImageResponse>>> getAsImageList(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                       @PathVariable Long asId){
        List<AsImageResponse> response = orderService.getAsImageList(customUserDetails, asId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }


    @PutMapping("/as/status")
    @Operation(summary = "✅ A/S 요청 상태 변경 API, 토큰 O")
    public ResponseEntity<ApiResponse<AsChangeStatusResponse>> changeAsStatus(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                              @RequestBody AsChangeStatusRequest request){
        AsChangeStatusResponse response = orderService.changeAsStatus(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/tax/search")
    @Operation(summary = "✅ 세금계산서 발급 리스트 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<TaxSearchResponse>>> getTaxList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody TaxSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable)
    {
        SearchResult<TaxSearchResponse> response = orderService.getTaxList(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/state/search")
    @Operation(summary = "✅ 거래명세서 리스트 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<StateSearchResponse>>> getStateList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody StateSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable)
    {
        SearchResult<StateSearchResponse> response = orderService.getStateList(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/state/supply/{orderId}")
    @Operation(summary = "✅ 거래명세서 직접 등록 시 공급자에 들어갈 정보 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<SupplyInfoResponse>> getInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                   @PathVariable Long orderId){
        SupplyInfoResponse response = orderService.getInfo(customUserDetails, orderId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/state/supply/{orderId}")
    @Operation(summary = "✅ 거래명세서 공급자 정보 수정 및 저장 API, 토큰 O")
    public ResponseEntity<ApiResponse<SupplyInfoResponse>> updateInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                      @RequestBody SupplyInfoRequest request,
                                                                      @PathVariable Long orderId){
        SupplyInfoResponse response = orderService.updateInfo(customUserDetails,request, orderId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/state/info/{orderId}")
    @Operation(summary = "✅ 거래명세서 내용 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<StatementResponse>> getStatement(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                      @PathVariable Long orderId){
        StatementResponse response = orderService.getStatement(customUserDetails, orderId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

}
