package livart.shop.domain.promotion;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import livart.shop.domain.promotion.dto.response.PromotionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class PromotionService {

    @PersistenceContext
    private EntityManager em;

    @Transactional(readOnly = true)
    public List<PromotionResponse> getAnnouncementsAsPromotions(boolean activeOnly, int limit) {
        // announcements: id, title, content, is_hidden, created_at ...
        String sql = """
            SELECT id, title, content
              FROM announcements
             WHERE (:activeOnly = FALSE OR is_hidden = FALSE)
             ORDER BY created_at DESC, id DESC
             LIMIT :limit
        """;
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("activeOnly", activeOnly)
                .setParameter("limit", limit)
                .getResultList();

        List<PromotionResponse> list = new ArrayList<>();
        for (Object[] r : rows) {
            Long id = ((Number) r[0]).longValue();
            String title = (String) r[1];
            String content = (String) r[2];
            String summary = summarize(content, 120); // 텍스트 요약만(이미지/썸네일 구조 없음)

            list.add(new PromotionResponse(
                    id, title, summary,
                    null,        // thumbUrl: ERD 미존재
                    id,          // noticeId: 공지 PK 그대로
                    null         // externalUrl: ERD 미존재
            ));
        }
        if (list.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "활성 프로모션(공지)이 없습니다.");
        }
        return list;
    }

    private String summarize(String content, int maxLen) {
        if (content == null) return null;
        String plain = content.replaceAll("\\<.*?\\>", ""); // 혹시 HTML이면 태그 제거
        return plain.length() <= maxLen ? plain : plain.substring(0, maxLen) + "...";
    }
}