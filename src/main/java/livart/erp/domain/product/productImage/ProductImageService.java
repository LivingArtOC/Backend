package livart.erp.domain.product.productImage;

import livart.common.Auth.CustomUserDetails;
import livart.common.domain.product.entity.Product;
import livart.common.domain.product.entity.ProductImage;
import livart.common.domain.product.repository.ProductImageRepository;
import livart.common.service.GlobalService;
import livart.erp.domain.product.productImage.ProductImageRequest;
import livart.erp.domain.product.productImage.ProductImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final GlobalService globalService;
    private final ProductImageRepository productImageRepository;

    @Transactional
    public List<ProductImageResponse> saveProductImage(CustomUserDetails customUserDetails, Product savedProduct, List<ProductImageRequest> productImages){
        globalService.validateAdmin(customUserDetails);

        List<ProductImage> productImageList = productImageRepository.saveAll(productImages.stream()
                .map(productImage -> ProductImage.builder()
                        .orderIndex(productImage.getOrderIndex())
                        .imageType(productImage.getImageType())
                        .imageUrl(productImage.getImageUrl())
                        .fileName(productImage.getFileName())
                        .detailText(productImage.getDetailText())
                        .updatedBy(customUserDetails.getId())
                        .product(savedProduct)
                        .build()
                ).collect(Collectors.toList()));

        savedProduct.getProductImages().addAll(productImageList);

        List<ProductImageResponse> productImageResponses = productImageList.stream()
                .map(productImage -> ProductImageResponse.builder()
                        .productImageId(productImage.getId())
                        .imageType(productImage.getImageType())
                        .orderIndex(productImage.getOrderIndex())
                        .imageUrl(productImage.getImageUrl())
                        .fileName(productImage.getFileName())
                        .detailText(productImage.getDetailText())
                        .build()
                ).collect(Collectors.toList());
        return productImageResponses;
    }

    public List<ProductImageResponse> getProductImage(CustomUserDetails customUserDetails, Product product){
        globalService.validateAdmin(customUserDetails);

        List<ProductImageResponse> productImageResponses = productImageRepository.findByProduct(product).stream()
                .map(productImage -> ProductImageResponse.builder()
                        .productImageId(productImage.getId())
                        .imageType(productImage.getImageType())
                        .orderIndex(productImage.getOrderIndex())
                        .imageUrl(productImage.getImageUrl())
                        .fileName(productImage.getFileName())
                        .detailText(productImage.getDetailText())
                        .build()
                ).collect(Collectors.toList());
        return productImageResponses;
    }
    @Transactional
    public List<ProductImageResponse> updateProductImage(CustomUserDetails customUserDetails, Product product, List<ProductImageRequest> images){
        globalService.validateAdmin(customUserDetails);

        List<ProductImage> productImageList = productImageRepository.saveAll(images.stream()
                .map(productImage -> ProductImage.builder()
                        .imageType(productImage.getImageType())
                        .imageUrl(productImage.getImageUrl())
                        .orderIndex(productImage.getOrderIndex())
                        .fileName(productImage.getFileName())
                        .detailText(productImage.getDetailText())
                        .updatedBy(customUserDetails.getId())
                        .product(product)
                        .build()
                ).collect(Collectors.toList()));

        product.getProductImages().addAll(productImageList);

        List<ProductImageResponse> productImageResponses = productImageList.stream()
                .map(productImage -> ProductImageResponse.builder()
                        .productImageId(productImage.getId())
                        .imageType(productImage.getImageType())
                        .orderIndex(productImage.getOrderIndex())
                        .imageUrl(productImage.getImageUrl())
                        .fileName(productImage.getFileName())
                        .detailText(productImage.getDetailText())
                        .build()
                ).collect(Collectors.toList());
        return productImageResponses;

    }

    @Transactional
    public void deleteImage(Product product){
        productImageRepository.deleteAllByProduct(product);
    }

}
