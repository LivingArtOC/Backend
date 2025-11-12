package livart.shop.domain.home;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import livart.shop.domain.home.dto.response.HomeResponse;

import java.util.List;

@Tag(name = "메인페이지 API ")
@Validated
@CrossOrigin("*")
@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @Operation(summary = "홈 화면 데이터 집계", description = "배너/카테고리 트리/카테고리별 Top-N 상품/공지(프로모션) 집계")
    @GetMapping
    public HomeResponse getHome(
            @Parameter(description = "배너 위치: MAIN | PRODUCT")
            @RequestParam(defaultValue = "MAIN") String placement,

            @Parameter(description = "카테고리 트리 깊이(>=1)")
            @RequestParam(defaultValue = "1") @Min(1) int categoriesDepth,

            @Parameter(description = "집계 대상 카테고리 ID 목록(최소 1개)")
            @RequestParam(name = "categoryId") List<Long> categoryIds,

            @Parameter(description = "카테고리별 상품 상위 N개")
            @RequestParam(defaultValue = "6") @Min(1) int productLimit,

            @Parameter(description = "판매 가능 상품만")
            @RequestParam(defaultValue = "true") boolean availableOnly,

            @Parameter(description = "활성 공지만")
            @RequestParam(defaultValue = "true") boolean promoActive,

            @Parameter(description = "공지 노출 상한")
            @RequestParam(defaultValue = "3") @Min(1) int promoLimit
    ) {
        return homeService.buildHome(
                placement, categoriesDepth, categoryIds,
                productLimit, availableOnly, promoActive, promoLimit
        );
    }
}