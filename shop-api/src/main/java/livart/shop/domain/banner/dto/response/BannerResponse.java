package livart.shop.domain.banner.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BannerResponse(
        Long id,
        String title,          // ERD에 없으므로 null → 자동 미포함
        String imageUrl,
        String linkType,       // 'link' 고정 (direct_url 존재)
        String linkTarget,
        Integer sortOrder,
        String validFrom,      // ERD 미지원
        String validTo         // ERD 미지원
) {}