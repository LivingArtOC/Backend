package livart.shop.domain.category;

import io.swagger.v3.oas.annotations.tags.Tag;
import livart.shop.domain.category.dto.response.CategoryNodeResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "메인페이지 제품 관련 API ")
@CrossOrigin("*")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // depth=1 (루트 레벨만)
    @GetMapping
    @Transactional(readOnly = true)
    public List<CategoryNodeResponse> getRoot(@RequestParam(name = "depth", defaultValue = "1") int depth) {
        return categoryService.getTree(depth);
    }

    // 트리 모드 (최대 depth 지정)
    @GetMapping("/tree")
    @Transactional(readOnly = true)
    public List<CategoryNodeResponse> getTree(@RequestParam(name = "maxDepth", defaultValue = "2") int maxDepth) {
        return categoryService.getTree(maxDepth);
    }
}