package livart.erp.domain.product.category;

import livart.common.Auth.CustomUserDetails;
import livart.common.domain.product.entity.Category;
import livart.common.domain.product.repository.CategoryRepository;
import livart.common.service.GlobalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final GlobalService globalService;

    @Transactional
    public List<CategoryTreeResponse> categoryRegister(CustomUserDetails customUserDetails, List<CategoryRequest> requests) {
        globalService.validateAdmin(customUserDetails);

        for (CategoryRequest request : requests) {
            saveCategoryRecursive(request, null);
        }

        return getCategoryList(customUserDetails);
    }

    private void saveCategoryRecursive(CategoryRequest request, Category parent) {
        Category category = Category.builder()
                .categoryName(request.getCategoryName())
                .depth(request.getDepth())
                .parent(parent)
                .build();
        categoryRepository.save(category);

        if (request.getChild() != null) {
            for (CategoryRequest childRequest : request.getChild()) {
                saveCategoryRecursive(childRequest, category);
            }
        }
    }

    public List<CategoryTreeResponse> getCategoryList(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        List<Category> allCategories = categoryRepository.findAll();

        Map<Long, List<Category>> groupedByParent = allCategories.stream()
                .filter(c -> c.getParent() != null)
                .collect(Collectors.groupingBy(c -> c.getParent().getId()));

        return allCategories.stream()
                .filter(c -> c.getParent() == null)
                .map(root -> buildTree(root, groupedByParent))
                .collect(Collectors.toList());
    }

    private CategoryTreeResponse buildTree(Category category, Map<Long, List<Category>> groupedByParent) {
        List<CategoryTreeResponse> children = Optional.ofNullable(groupedByParent.get(category.getId()))
                .orElse(List.of())
                .stream()
                .map(child -> buildTree(child, groupedByParent))
                .collect(Collectors.toList());

        return CategoryTreeResponse.builder()
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .depth(category.getDepth())
                .children(children)
                .build();
    }
}
