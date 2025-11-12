package livart.shop.domain.product.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductCardResponse(
        Long productId,
        String name,                 // products.product_name
        String brand,                // products.brand
        String thumbnailUrl,         // product_images(THUMBNAIL)
        BigDecimal priceOriginal,    // products.regular_price
        BigDecimal priceSale,        // products.price
        BigDecimal discountRate,     // products.discount_rate
        List<ProductColorVariant> colorVariants
) {}