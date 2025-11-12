package livart.shop.domain.cart.dto.request;

import jakarta.validation.constraints.Min;

public record UpdateQuantityRequest(@Min(1) Integer quantity) {}