package livart.shop.domain.promotion;

import io.swagger.v3.oas.annotations.tags.Tag;
import livart.shop.domain.promotion.dto.response.PromotionResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@Tag(name = "메인페이지 프로모션 API ")
@CrossOrigin("*")
public class PromotionController {

    private final PromotionService promotionService;
    public PromotionController(PromotionService promotionService) { this.promotionService = promotionService; }

    @GetMapping
    @Transactional(readOnly = true)
    public List<PromotionResponse> getPromotions(
            @RequestParam(name = "placement", defaultValue = "HOME") String placement, // ERD엔 분류 없음 → 값 무시
            @RequestParam(name = "active", defaultValue = "true") boolean active,
            @RequestParam(name = "limit", defaultValue = "3") int limit
    ) {
        return promotionService.getAnnouncementsAsPromotions(active, limit);
    }
}