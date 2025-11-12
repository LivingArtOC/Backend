package livart.shop.domain.cart.dto.request;

import java.util.List;

public record UpdateOptionsRequest(
        List<Long> optionValueIds     // DetailedOption.id 리스트
) {}