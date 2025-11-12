package livart.shop.domain.home;

import livart.shop.domain.banner.BannerService;
import livart.shop.domain.banner.dto.response.BannerResponse;
import livart.shop.domain.category.CategoryService;
import livart.shop.domain.category.dto.response.CategoryNodeResponse;
import livart.shop.domain.category.dto.response.CategoryProductsResponse;
import livart.shop.domain.home.dto.response.HomeResponse;
import livart.shop.domain.product.ProductService;
import livart.shop.domain.product.dto.response.ProductCardResponse;
import livart.shop.domain.promotion.PromotionService;
import livart.shop.domain.promotion.dto.response.PromotionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class HomeService {

    private final BannerService bannerService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final PromotionService promotionService;

    public HomeService(
            BannerService bannerService,
            CategoryService categoryService,
            ProductService productService,
            PromotionService promotionService
    ) {
        this.bannerService = bannerService;
        this.categoryService = categoryService;
        this.productService = productService;
        this.promotionService = promotionService;
    }

    public HomeResponse buildHome(
            String placement,
            int categoriesDepth,
            List<Long> categoryIds,
            int productLimit,
            boolean availableOnly,
            boolean promoActive,
            int promoLimit
    ) {
        // 1) Banners
        List<BannerResponse> banners = switch ((placement == null ? "MAIN" : placement).toUpperCase()) {
            case "MAIN" -> bannerService.getMainBanners();
            case "PRODUCT" -> bannerService.getProductBanners();
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "placement는 MAIN 또는 PRODUCT만 지원합니다."
            );
        };
        if (banners == null || banners.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "배너가 없습니다.");
        }

        // 2) Categories (depth 계층)
        List<CategoryNodeResponse> categories = categoryService.getTree(categoriesDepth);
        if (categories == null || categories.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리가 없습니다.");
        }

        // 3) Products Top-N by category (views 미지원 → ProductService 쿼리 기본 정책 준수)
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "categoryId는 최소 1개 이상 필수입니다.");
        }
        List<CategoryProductsResponse> productsByCategory = new ArrayList<>();
        for (Long cid : categoryIds) {
            List<ProductCardResponse> products = productService.findTopByCategory(cid, productLimit, availableOnly);
            if (products == null || products.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리(" + cid + ")에 상품이 없습니다.");
            }
            productsByCategory.add(new CategoryProductsResponse(cid, products));
        }

        // 4) Promotions (공지)
        List<PromotionResponse> promotions = promotionService.getAnnouncementsAsPromotions(promoActive, promoLimit);
        if (promotions == null || promotions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "활성 프로모션(공지)이 없습니다.");
        }

        return new HomeResponse(
                (placement == null ? "MAIN" : placement).toUpperCase(),
                categoriesDepth,
                productLimit,
                availableOnly,
                promoActive,
                promoLimit,
                banners,
                categories,
                productsByCategory,
                promotions
        );
    }
}