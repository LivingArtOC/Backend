package livart.shop.domain.banner;

import io.swagger.v3.oas.annotations.tags.Tag;
import livart.shop.domain.banner.dto.response.BannerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Tag(name = "메인 배너 API ")
@RequestMapping("/api/banners")
@CrossOrigin("*")
public class BannerController {

    private final BannerService bannerService;

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<BannerResponse> getBanners(
            @RequestParam(name = "placement") String placement,
            @RequestParam(name = "active", required = false) Boolean active // ERD에 없음 → 사용 시 400
    ) {
        if (active != null) {
            // main_banners/product_banners에는 is_active, valid 기간 컬럼이 없음 → 임의 판단 금지
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "active 필터는 지원되지 않습니다(ERD에 활성/기간 컬럼 없음).");
        }

        if ("MAIN".equalsIgnoreCase(placement)) {
            return bannerService.getMainBanners();
        } else if ("PRODUCT".equalsIgnoreCase(placement)) {
            return bannerService.getProductBanners();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "placement는 MAIN 또는 PRODUCT만 지원합니다.");
        }
    }
}