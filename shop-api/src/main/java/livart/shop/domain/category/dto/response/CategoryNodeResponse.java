package livart.shop.domain.category.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * 카테고리 트리 노드 응답 DTO.
 * - ERD에는 slug 컬럼이 없지만, 프론트/스펙 호환을 위해 필드만 제공하고 값은 null로 둘 수 있음.
 * - JsonInclude.NON_NULL 로 null 필드는 자동 제외됨.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryNodeResponse(
        Long categoryId,
        String name,                 // ERD: category_name
        String slug,                 // ERD 미존재 → 필요 시 null
        List<CategoryNodeResponse> children
) {}