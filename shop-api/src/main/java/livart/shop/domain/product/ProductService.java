package livart.shop.domain.product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import livart.shop.domain.product.dto.response.ProductCardResponse;
import livart.shop.domain.product.dto.response.ProductColorVariant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ProductService {

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public List<ProductCardResponse> findTopByCategory(Long categoryId, int limit, boolean availableOnly) {
        // products + product_category_mapping + product_images(THUMBNAIL)
        // 노출/재고 필터: display_status = 'VISIBLE', stock_status = 'IN_STOCK'
        String base = """
            SELECT p.id, p.product_name, p.brand, p.regular_price, p.price, p.discount_rate,
                   (SELECT pi.image_url
                      FROM product_images pi
                     WHERE pi.product_id = p.id
                       AND pi.image_type = 'THUMBNAIL'
                     ORDER BY pi.display_order ASC, pi.id ASC
                     LIMIT 1) AS thumb
              FROM products p
              JOIN product_category_mapping pcm ON pcm.product_id = p.id
             WHERE pcm.category_id = :categoryId
        """;

        String filter = "";
        if (availableOnly) {
            filter = " AND p.display_status = 'VISIBLE' AND p.stock_status = 'IN_STOCK' ";
        }

        // 최신 업데이트 순(views 미지원)
        String orderLimit = " ORDER BY p.updated_at DESC, p.id DESC LIMIT :limit ";

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(base + filter + orderLimit)
                .setParameter("categoryId", categoryId)
                .setParameter("limit", limit)
                .getResultList();

        List<ProductCardResponse> result = new ArrayList<>();
        for (Object[] r : rows) {
            Long productId = ((Number) r[0]).longValue();
            String name = (String) r[1];
            String brand = (String) r[2];
            BigDecimal priceOriginal = (BigDecimal) r[3]; // regular_price
            BigDecimal priceSale = (BigDecimal) r[4];     // price
            BigDecimal discountRate = (BigDecimal) r[5];  // discount_rate
            String thumb = (String) r[6];

            List<ProductColorVariant> colors = findColorVariants(productId, thumb);

            result.add(new ProductCardResponse(
                    productId,
                    name,
                    brand,
                    thumb,
                    priceOriginal,
                    priceSale,
                    discountRate,
                    colors
            ));
        }
        if (result.isEmpty()) {
            // 임시 데이터 금지: 비어있으면 404
            throw new NoSuchElementException("해당 카테고리의 상품이 없습니다.");
        }
        return result;
    }

    // 색상 옵션 조회 (product_options.option_category='색상')
    // ERD에 색상-이미지 매핑은 없음 → 각 색상 썸네일은 제품 썸네일을 공통 사용
    @Transactional(readOnly = true)
    public List<ProductColorVariant> findColorVariants(Long productId, String fallbackThumb) {
        String sql = """
            SELECT pov.value_name
              FROM product_options po
              JOIN product_option_values pov ON pov.option_id = po.id
             WHERE po.product_id = :pid
               AND po.option_category = '색상'
             ORDER BY pov.value_name ASC, pov.id ASC
        """;
        @SuppressWarnings("unchecked")
        List<String> names = em.createNativeQuery(sql)
                .setParameter("pid", productId)
                .getResultList();

        List<ProductColorVariant> list = new ArrayList<>();
        for (String n : names) {
            list.add(new ProductColorVariant(n, fallbackThumb));
        }
        return list;
    }
}