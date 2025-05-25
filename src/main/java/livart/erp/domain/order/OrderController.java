package livart.erp.domain.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.Auth.CustomUserDetails;
import livart.common.dto.response.ApiResponse;
import livart.common.mapper.SearchResult;
import livart.erp.domain.order.dto.request.OrderSearchRequest;
import livart.erp.domain.order.dto.request.UpdateStatusRequest;
import livart.erp.domain.order.dto.response.OrderAllResponse;
import livart.erp.domain.order.dto.response.OrderIndResponse;

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
    @Operation(summary = "✅ 개별 주문 리스트 검색 API, 토큰 O")
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
    @Operation(summary = "✅ 결제 중단/실패 리스트에서 일괄 삭제 API, 토큰 O")
    public ResponseEntity<ApiResponse<String>> deleteItem(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                          @RequestBody List<Long> idList){
        orderService.deleteItem(customUserDetails, idList);
        return ResponseEntity.ok(ApiResponse.ok("상태가 변경되었습니다."));
    }

}
