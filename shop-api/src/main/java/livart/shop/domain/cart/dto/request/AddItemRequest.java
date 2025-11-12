package livart.shop.domain.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AddItemRequest(
        @NotNull Long productId,
        @Min(1) Integer quantity,
        List<Long> optionValueIds     // DetailedOption.id 리스트
) {}