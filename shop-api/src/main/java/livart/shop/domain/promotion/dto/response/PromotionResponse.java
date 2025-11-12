package livart.shop.domain.promotion.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PromotionResponse(
        Long id,
        String title,
        String summary,
        String thumbUrl,   // ERD에 없음 → null 시 자동 미포함
        Long noticeId,     // announcements.id
        String externalUrl // ERD에 없음
) {}