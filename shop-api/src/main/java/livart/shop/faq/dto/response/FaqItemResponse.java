package livart.shop.faq.dto.response;

import java.time.LocalDateTime;

public record FaqItemResponse(
        Long id,
        String category,
        String question,
        String answer,
        LocalDateTime updatedAt
) {}