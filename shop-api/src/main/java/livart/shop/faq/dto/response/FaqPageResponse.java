package livart.shop.faq.dto.response;

import livart.common.domain.support.faq.entity.FAQ;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public record FaqPageResponse(
        List<FaqItemResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static FaqPageResponse from(Page<FAQ> p) {
        var items = p.getContent().stream()
                .map(f -> new FaqItemResponse(
                        f.getId(),
                        f.getType() == null ? "OTHERS" : f.getType().name(),
                        f.getQuestion(),
                        f.getAnswer(),
                        lastUpdatedOf(f)
                ))
                .toList();

        return new FaqPageResponse(items, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }

    private static LocalDateTime lastUpdatedOf(FAQ f) {
        // BaseTime의 updatedAt 우선, 없으면 createdAt 사용
        return f.getUpdatedAt() != null ? f.getUpdatedAt() : f.getCreatedAt();
    }
}