package livart.common.dto.enums.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductExcelField {
    IMAGE("image", "상품 이미지"),
    BRAND("brand", "브랜드"),
    NAME("name", "상품명"),
    CODE("code", "상품 코드"),
    DEL_PRICE("delPrice", "납품가"),
    SALE_PRICE("salePrice", "판매가"),
    ORIGINAL_PRICE("originalPrice", "정가"),
    REGISTER_DATE("registerDate", "등록일"),
    STOCK("stock", "품절 여부");

    private final String field;
    private final String label;
}
