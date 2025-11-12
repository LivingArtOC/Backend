package livart.shop.domain.cart.dto.response;

import java.util.List;

public record CartListResponse(
        List<CartItemResponse> items,
        CartSummary summary
) {
    public record CartSummary(Long totalListAmount, Long totalDiscountAmount, Long totalPayableAmount) {}
}