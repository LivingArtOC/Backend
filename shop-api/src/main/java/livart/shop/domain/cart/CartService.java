package livart.shop.domain.cart;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import livart.common.domain.product.entity.Product;
import livart.common.domain.product.repository.ProductRepository;
import livart.common.domain.support.estimate.entity.Estimate;
import livart.common.domain.support.estimate.entity.EstimateItem;
import livart.common.domain.support.estimate.repository.EstimateItemRepository;
import livart.common.domain.support.estimate.repository.EstimateRepository;
import livart.common.dto.enums.product.BrandType;
import livart.shop.domain.cart.dto.request.AddItemRequest;
import livart.shop.domain.cart.dto.response.CartItemResponse;
import livart.shop.domain.cart.dto.response.CartListResponse;
import livart.shop.domain.cart.dto.response.CartListResponse.CartSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final EstimateRepository estimateRepository;
    private final EstimateItemRepository estimateItemRepository;
    private final ProductRepository productRepository;

    @PersistenceContext
    private EntityManager em;

    /** 장바구니 목록 */
    @Transactional
    public CartListResponse getCart(Long userId) {
        Estimate cart = findOrCreateCart(userId);
        List<EstimateItem> items = em.createQuery(
                "select i from EstimateItem i where i.estimate.id = :eid order by i.id desc",
                EstimateItem.class
        ).setParameter("eid", cart.getId()).getResultList();

        var mapped = items.stream().map(this::toResponse).collect(Collectors.toList());
        return new CartListResponse(mapped, new CartSummary(0L, 0L, 0L));
    }

    /** 담기 */
    public CartItemResponse addItem(Long userId, AddItemRequest req) {
        if (req.productId() == null || req.quantity() == null || req.quantity() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "상품/수량이 잘못되었습니다.");
        }
        Product product = productRepository.findById(req.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."));

        String optionCode = stableCsv(req.optionValueIds());
        String hash = product.getId() + "#" + optionCode;

        Estimate cart = findOrCreateCart(userId);

        // 동일 조합 존재
        List<EstimateItem> dupList = em.createQuery(
                        "select i from EstimateItem i where i.estimate.id = :eid and i.hashCode = :hash",
                        EstimateItem.class
                ).setParameter("eid", cart.getId())
                .setParameter("hash", hash)
                .setMaxResults(1)
                .getResultList();

        if (!dupList.isEmpty()) {
            EstimateItem item = dupList.get(0);
            int newQty = (item.getQuantity() == null ? 0 : item.getQuantity()) + req.quantity();

            em.createQuery("update EstimateItem i set i.quantity = :q, i.updatedBy = :u where i.id = :id")
                    .setParameter("q", newQty)
                    .setParameter("u", userId)
                    .setParameter("id", item.getId())
                    .executeUpdate();

            EstimateItem updated = estimateItemRepository.findById(item.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "항목을 찾을 수 없습니다."));
            return toResponse(updated);
        }

        // Product 표시 필드(리플렉션으로 안전 조회)
        String productName = callStringGetter(product,
                "getProductName", "getName", "getNameKo", "getTitle", "getTitleKo");
        String thumbUrl = callStringGetter(product,
                "getThumbnailUrl", "getThumbNailImgUrl", "getMainImageUrl", "getImageUrl");
        BrandType brand = callBrand(product, "getBrand", "getBrandType");

        // 신규 추가 (표시필드는 null 가능)
        EstimateItem newItem = EstimateItem.builder()
                .productId(product.getId())
                .productName(productName)
                .thumbNailImgUrl(thumbUrl)
                .brand(brand)
                .quantity(req.quantity())
                .optionCode(optionCode)
                .hashCode(hash)
                .estimate(cart)
                .updatedBy(userId)
                .build();

        estimateItemRepository.save(newItem);
        return toResponse(newItem);
    }

    /** 수량 변경 */
    public CartItemResponse updateQuantity(Long userId, Long itemId, int quantity) {
        if (quantity < 1) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수량은 1 이상이어야 합니다.");
        Estimate cart = findOrCreateCart(userId);

        int updated = em.createQuery(
                        "update EstimateItem i set i.quantity = :q, i.updatedBy = :u " +
                                "where i.id = :id and i.estimate.id = :eid"
                ).setParameter("q", quantity)
                .setParameter("u", userId)
                .setParameter("id", itemId)
                .setParameter("eid", cart.getId())
                .executeUpdate();

        if (updated == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "항목을 찾을 수 없습니다.");

        EstimateItem item = estimateItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "항목을 찾을 수 없습니다."));
        return toResponse(item);
    }

    /** 특정 아이템 옵션 조회 → optionCode 파싱 */
    public List<Long> getOptionValueIds(Long userId, Long itemId) {
        EstimateItem item = loadOwnedItem(userId, itemId);
        return parseCsv(item.getOptionCode());
    }

    /** 특정 아이템 옵션 수정 (동일 조합 존재 시 병합) */
    public CartItemResponse updateOptions(Long userId, Long itemId, List<Long> optionValueIds) {
        EstimateItem item = loadOwnedItem(userId, itemId);

        String newCode = stableCsv(optionValueIds);
        String newHash = item.getProductId() + "#" + newCode;

        // 동일 조합 라인(본인 제외)
        List<EstimateItem> dupList = em.createQuery(
                        "select i from EstimateItem i where i.estimate.id = :eid and i.hashCode = :hash and i.id <> :id",
                        EstimateItem.class
                ).setParameter("eid", item.getEstimate().getId())
                .setParameter("hash", newHash)
                .setParameter("id", item.getId())
                .setMaxResults(1)
                .getResultList();

        if (!dupList.isEmpty()) {
            EstimateItem target = dupList.get(0);
            int mergedQty = (target.getQuantity() == null ? 0 : target.getQuantity())
                    + (item.getQuantity() == null ? 0 : item.getQuantity());

            em.createQuery("update EstimateItem i set i.quantity = :q, i.updatedBy = :u where i.id = :tid")
                    .setParameter("q", mergedQty)
                    .setParameter("u", userId)
                    .setParameter("tid", target.getId())
                    .executeUpdate();

            em.createQuery("delete from EstimateItem i where i.id = :cid")
                    .setParameter("cid", item.getId())
                    .executeUpdate();

            EstimateItem refreshed = estimateItemRepository.findById(target.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "항목을 찾을 수 없습니다."));
            return toResponse(refreshed);
        }

        int updated = em.createQuery(
                        "update EstimateItem i set i.optionCode = :code, i.hashCode = :hash, i.updatedBy = :u " +
                                "where i.id = :id and i.estimate.id = :eid"
                ).setParameter("code", newCode)
                .setParameter("hash", newHash)
                .setParameter("u", userId)
                .setParameter("id", item.getId())
                .setParameter("eid", item.getEstimate().getId())
                .executeUpdate();

        if (updated == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "항목을 찾을 수 없습니다.");

        EstimateItem refreshed = estimateItemRepository.findById(item.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "항목을 찾을 수 없습니다."));
        return toResponse(refreshed);
    }

    /** 단건 삭제 */
    public void deleteItem(Long userId, Long itemId) {
        Estimate cart = findOrCreateCart(userId);
        int deleted = em.createQuery(
                        "delete from EstimateItem i where i.id = :id and i.estimate.id = :eid"
                ).setParameter("id", itemId)
                .setParameter("eid", cart.getId())
                .executeUpdate();
        if (deleted == 0) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "항목을 찾을 수 없습니다.");
    }

    /** 전체 삭제 */
    public void deleteAll(Long userId) {
        Estimate cart = findOrCreateCart(userId);
        em.createQuery("delete from EstimateItem i where i.estimate.id = :eid")
                .setParameter("eid", cart.getId())
                .executeUpdate();
    }

    // ==================== 내부 유틸 ====================

    /** 현재 로그인 사용자의 카트(Estimate) 조회/생성 */
    private Estimate findOrCreateCart(Long userId) {
        List<Estimate> found = em.createQuery(
                "select e from Estimate e where e.createdBy = :uid order by e.id desc",
                Estimate.class
        ).setParameter("uid", userId).setMaxResults(1).getResultList();

        if (!found.isEmpty()) return found.get(0);

        Estimate e = Estimate.builder()
                .createdBy(userId)
                .memo("CART")
                .build();
        return estimateRepository.save(e);
    }

    /** 소유권 검증 포함 아이템 로드 */
    private EstimateItem loadOwnedItem(Long userId, Long itemId) {
        Estimate cart = findOrCreateCart(userId);
        EstimateItem item = estimateItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "항목을 찾을 수 없습니다."));
        if (!Objects.equals(item.getEstimate().getId(), cart.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 사용자의 장바구니 항목이 아닙니다.");
        }
        return item;
    }

    /** 옵션값 ID 리스트 → 정렬된 CSV */
    private static String stableCsv(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return "";
        return ids.stream().sorted().map(String::valueOf).collect(Collectors.joining(","));
    }

    /** CSV → ID 리스트 */
    private static List<Long> parseCsv(String csv) {
        if (csv == null || csv.isBlank()) return Collections.emptyList();
        return Arrays.stream(csv.split(","))
                .filter(s -> !s.isBlank())
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    /** 엔티티 → 응답 DTO */
    private CartItemResponse toResponse(EstimateItem i) {
        return new CartItemResponse(
                i.getId(),
                i.getProductId(),
                i.getProductName(),
                i.getThumbNailImgUrl(),
                i.getOptionCode(),
                i.getBrand() != null ? i.getBrand().name() : null,
                i.getQuantity()
        );
    }

    // ===== 리플렉션 보조: Product에서 문자열/브랜드 안전 조회 =====
    private static String callStringGetter(Object target, String... methodNames) {
        if (target == null) return null;
        for (String name : methodNames) {
            try {
                Method m = target.getClass().getMethod(name);
                Object v = m.invoke(target);
                if (v != null) return v.toString();
            } catch (Exception ignored) {}
        }
        return null;
    }

    private static BrandType callBrand(Object target, String... methodNames) {
        if (target == null) return null;
        for (String name : methodNames) {
            try {
                Method m = target.getClass().getMethod(name);
                Object v = m.invoke(target);
                if (v instanceof BrandType bt) return bt;
            } catch (Exception ignored) {}
        }
        return null;
    }
}