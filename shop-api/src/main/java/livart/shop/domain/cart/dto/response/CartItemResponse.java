package livart.shop.domain.cart.dto.response;

public record CartItemResponse(
        Long itemId,
        Long productId,
        String productName,
        String thumbnailUrl,
        String optionCode,   // 정렬된 옵션값ID CSV (라벨은 EstimateItemOption에서 필요 시 조회)
        String brandName,    // enum name()
        Integer quantity
) {}