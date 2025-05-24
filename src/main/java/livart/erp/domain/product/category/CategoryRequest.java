package livart.erp.domain.product.category;

import lombok.Getter;

import java.util.List;

@Getter
public class CategoryRequest {
    private Integer depth;
    private String categoryName;
    private List<CategoryRequest> child;
}
