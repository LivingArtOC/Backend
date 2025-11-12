package livart.shop.domain.home.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import livart.shop.domain.banner.dto.response.BannerResponse;
import livart.shop.domain.category.dto.response.CategoryNodeResponse;
import livart.shop.domain.category.dto.response.CategoryProductsResponse;
import livart.shop.domain.promotion.dto.response.PromotionResponse;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record HomeResponse(
        String placement,        // MAIN | PRODUCT
        int categoryDepth,
        int productLimit,
        boolean availableOnly,
        boolean promoActive,
        int promoLimit,

        List<BannerResponse> banners,
        List<CategoryNodeResponse> categories,
        List<CategoryProductsResponse> productsByCategory,
        List<PromotionResponse> promotions
) {}