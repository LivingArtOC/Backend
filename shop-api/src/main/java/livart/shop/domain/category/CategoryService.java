package livart.shop.domain.category;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import livart.shop.domain.category.dto.response.CategoryNodeResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public List<CategoryNodeResponse> getTree(int maxDepth) {
        // categories: id, parent_id, category_name, depth, display_order
        String sql = """
            SELECT id, parent_id, category_name, depth
            FROM categories
            ORDER BY display_order ASC, id ASC
        """;
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql).getResultList();

        // build nodes
        Map<Long, CategoryNodeResponse> map = new LinkedHashMap<>();
        for (Object[] r : rows) {
            Long id = ((Number) r[0]).longValue();
            Long parentId = r[1] == null ? null : ((Number) r[1]).longValue();
            String name = (String) r[2];
            int depth = ((Number) r[3]).intValue();
            if (depth <= maxDepth) {
                map.put(id, new CategoryNodeResponse(id, name, null, null)); // children 나중에 채움
            }
        }

        // attach children
        Map<Long, List<CategoryNodeResponse>> childrenMap = new HashMap<>();
        for (Object[] r : rows) {
            Long id = ((Number) r[0]).longValue();
            Long parentId = r[1] == null ? null : ((Number) r[1]).longValue();
            int depth = ((Number) r[3]).intValue();

            if (!map.containsKey(id)) continue;
            if (parentId != null && map.containsKey(parentId) && depth <= maxDepth) {
                childrenMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(map.get(id));
            }
        }

        // set children
        map.replaceAll((id, node) -> new CategoryNodeResponse(
                node.categoryId(),
                node.name(),
                node.slug(), // slug은 ERD 미존재로 현재 null 유지
                childrenMap.getOrDefault(id, Collections.emptyList())
        ));

        // return roots
        return map.values().stream()
                .filter(n -> {
                    // parentId가 null인 것만 루트. rows에서 parentId 확인
                    // 빠른 판별 위해 다시 한 번 rows 참조
                    for (Object[] r : rows) {
                        Long rid = ((Number) r[0]).longValue();
                        if (!rid.equals(n.categoryId())) continue;
                        Object pid = r[1];
                        return pid == null;
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
}