package livart.shop.domain.product;

import io.swagger.v3.oas.annotations.tags.Tag;
import livart.shop.domain.product.dto.response.ProductCardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "메인페이지 제품 관련 API ")

@CrossOrigin("*")
public class ProductController {

    private final ProductService productService;
    public ProductController(ProductService productService) { this.productService = productService; }

    @GetMapping("/{categoryId}/products")
    @Transactional(readOnly = true)
    public List<ProductCardResponse> topProducts(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam(name = "limit", defaultValue = "6") int limit,
            @RequestParam(name = "sort", defaultValue = "views,desc") String sort,
            @RequestParam(name = "available", defaultValue = "true") boolean available
    ) {
        if (sort.toLowerCase().startsWith("views")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "sort=views,* 는 지원되지 않습니다(ERD에 조회수 구조 없음). " +
                            "필요 시 '판매량' 기준(order_items 집계)으로 확장해주세요.");
        }
        // 최신 등록/수정 순
        return productService.findTopByCategory(categoryId, limit, available);
    }
}