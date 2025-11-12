package livart.shop.domain.banner;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import livart.shop.domain.banner.dto.response.BannerResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BannerService {

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public List<BannerResponse> getMainBanners() {
        // ERD: main_banners(id, image_url, direct_url, order_index, ...)
        // 정렬 기준은 order_index
        String sql = """
            SELECT id, image_url, direct_url, order_index
            FROM main_banners
            ORDER BY order_index ASC
        """;
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql).getResultList();
        return rows.stream()
                .map(r -> new BannerResponse(
                        ((Number) r[0]).longValue(),
                        null, // title: ERD 미존재 → null 반환(응답에서 미포함)
                        (String) r[1],
                        "link", // linkType: direct_url만 존재 → 'link'로 고정(임의 URL 생성 아님)
                        (String) r[2],
                        ((Number) r[3]).intValue(),
                        null, null // validFrom/To: ERD 미존재
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BannerResponse> getProductBanners() {
        String sql = """
            SELECT id, image_url, direct_url, order_index
            FROM product_banners
            ORDER BY order_index ASC
        """;
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql).getResultList();
        return rows.stream()
                .map(r -> new BannerResponse(
                        ((Number) r[0]).longValue(),
                        null,
                        (String) r[1],
                        "link",
                        (String) r[2],
                        ((Number) r[3]).intValue(),
                        null, null
                ))
                .toList();
    }
}