package livart.erp.domain.product.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryTreeResponse {
    private Long categoryId;
    private String categoryName;
    private Integer depth;
    private List<CategoryTreeResponse> children;
}
