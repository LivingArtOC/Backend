package livart.erp.domain.product.productColor;

import livart.common.Auth.CustomUserDetails;
import livart.common.domain.product.entity.Product;
import livart.common.domain.product.entity.ProductColor;
import livart.common.domain.product.repository.ProductColorRepository;
import livart.common.service.GlobalService;
import livart.common.dto.request.product.ProductColorRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductColorService {
    private final GlobalService globalService;
    private final ProductColorRepository productColorRepository;

    @Transactional
    public List<ProductColorResponse> saveProductColor(CustomUserDetails customUserDetails, Product savedProduct, List<ProductColorRequest> productColors){
        globalService.validateAdmin(customUserDetails);
        List<ProductColorRequest> colorRequests = productColors.stream()
                .sorted(Comparator.comparingInt(ProductColorRequest::getOrderIndex))
                .collect(Collectors.toList());

        List<ProductColor> savedColor = productColorRepository.saveAll(colorRequests.stream()
                .map(color -> ProductColor.builder()
                        .colorType(color.getColorType())
                        .colorCode(color.getColorCode())
                        .orderIndex(color.getOrderIndex())
                        .updatedBy(customUserDetails.getId())
                        .product(savedProduct)
                        .build()
                ).collect(Collectors.toList()));

        savedProduct.getProductColors().addAll(savedColor);

        List<ProductColorResponse> productColorResponses = savedColor.stream()
                .sorted(Comparator.comparingInt(ProductColor::getOrderIndex))
                .map(color -> ProductColorResponse.builder()
                        .productColorId(color.getId())
                        .colorType(color.getColorType())
                        .colorCode(color.getColorCode())
                        .orderIndex(color.getOrderIndex())
                        .build())
                .collect(Collectors.toList());
        return productColorResponses;
    }

    public List<ProductColorResponse> getProductColor(CustomUserDetails customUserDetails, Product product){
        globalService.validateAdmin(customUserDetails);

        List<ProductColorResponse> productColorResponses = productColorRepository.findByProduct(product).stream()
                .sorted(Comparator.comparingInt(ProductColor::getOrderIndex))
                .map(color -> ProductColorResponse.builder()
                        .productColorId(color.getId())
                        .colorType(color.getColorType())
                        .colorCode(color.getColorCode())
                        .orderIndex(color.getOrderIndex())
                        .build())
                .collect(Collectors.toList());
        return productColorResponses;
    }

    @Transactional
    public List<ProductColorResponse> updateProductColor(CustomUserDetails customUserDetails, Product product, List<ProductColorRequest> colors){
        globalService.validateAdmin(customUserDetails);
        List<ProductColorRequest> colorRequests = colors.stream()
                .sorted(Comparator.comparingInt(ProductColorRequest::getOrderIndex))
                .collect(Collectors.toList());

        List<ProductColor> savedColor = productColorRepository.saveAll(colorRequests.stream()
                .map(color -> ProductColor.builder()
                        .colorType(color.getColorType())
                        .colorCode(color.getColorCode())
                        .orderIndex(color.getOrderIndex())
                        .updatedBy(customUserDetails.getId())
                        .product(product)
                        .build()
                ).collect(Collectors.toList()));

        product.getProductColors().addAll(savedColor);

        List<ProductColorResponse> productColorResponses = savedColor.stream()
                .sorted(Comparator.comparingInt(ProductColor::getOrderIndex))
                .map(color -> ProductColorResponse.builder()
                        .productColorId(color.getId())
                        .colorType(color.getColorType())
                        .colorCode(color.getColorCode())
                        .build())
                .collect(Collectors.toList());
        return productColorResponses;
    }

    @Transactional
    public void deleteColor(Product product){
        productColorRepository.deleteAllByProduct(product);
    }

}
