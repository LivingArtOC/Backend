package livart.shop.domain.product.dto.response;

public record ProductColorVariant(
        String colorCode,      // ERD엔 코드/hex 없음 → value_name을 그대로 노출
        String thumbnailUrl    // 색상-이미지 매핑 구조가 없어 제품 썸네일을 공통 사용
) {}