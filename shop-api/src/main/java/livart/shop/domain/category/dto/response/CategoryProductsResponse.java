package livart.shop.domain.category.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import livart.shop.domain.product.dto.response.ProductCardResponse;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryProductsResponse(
        Long categoryId,
        List<ProductCardResponse> products
) {}