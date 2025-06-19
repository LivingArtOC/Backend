package livart.erp.domain.product.productGuide;

import livart.common.Auth.CustomUserDetails;
import livart.common.domain.product.entity.Product;
import livart.common.domain.product.entity.ProductGuideInfo;
import livart.common.domain.product.repository.ProductGuideInfoRepository;
import livart.common.service.GlobalService;
import livart.common.dto.request.product.ProductGuideInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductGuideInfoService {
    private final GlobalService globalService;
    private final ProductGuideInfoRepository productGuideInfoRepository;

    @Transactional
    public List<ProductGuideInfoResponse> saveProductGuideInfo(CustomUserDetails customUserDetails, Product savedProduct, List<ProductGuideInfoRequest> productGuideInfos){
        globalService.validateAdmin(customUserDetails);

        List<ProductGuideInfo> productGuideInfoList = productGuideInfoRepository.saveAll(productGuideInfos.stream()
                .map(info -> ProductGuideInfo.builder()
                        .guide(info.getGuide())
                        .orderIndex(info.getOrderIndex())
                        .imageUrl(info.getImageUrl())
                        .fileName(info.getFileName())
                        .text(info.getText())
                        .updatedBy(customUserDetails.getId())
                        .product(savedProduct)
                        .build())
                .collect(Collectors.toList()));

        savedProduct.getProductGuideInfos().addAll(productGuideInfoList);

        List<ProductGuideInfoResponse> productGuideInfoResponses = productGuideInfoList.stream()
                .map(productGuideInfo -> ProductGuideInfoResponse.builder()
                        .guideId(productGuideInfo.getId())
                        .guide(productGuideInfo.getGuide())
                        .orderIndex(productGuideInfo.getOrderIndex())
                        .imageUrl(productGuideInfo.getImageUrl())
                        .fileName(productGuideInfo.getFileName())
                        .text(productGuideInfo.getText())
                        .build()).collect(Collectors.toList());

        return productGuideInfoResponses;
    }

    public List<ProductGuideInfoResponse> getProductGuideInfo(CustomUserDetails customUserDetails, Product product){
        globalService.validateAdmin(customUserDetails);

        List<ProductGuideInfoResponse> productGuideInfoResponses = productGuideInfoRepository.findByProduct(product).stream()
                .map(productGuideInfo -> ProductGuideInfoResponse.builder()
                        .guideId(productGuideInfo.getId())
                        .orderIndex(productGuideInfo.getOrderIndex())
                        .guide(productGuideInfo.getGuide())
                        .imageUrl(productGuideInfo.getImageUrl())
                        .fileName(productGuideInfo.getFileName())
                        .text(productGuideInfo.getText())
                        .build()).collect(Collectors.toList());

        return productGuideInfoResponses;
    }

    @Transactional
    public List<ProductGuideInfoResponse> updateProductGuideInfo(CustomUserDetails customUserDetails, Product product, List<ProductGuideInfoRequest> guideInfos) {
        globalService.validateAdmin(customUserDetails);

        List<ProductGuideInfo> productGuideInfoList = productGuideInfoRepository.saveAll(guideInfos.stream()
                .map(info -> ProductGuideInfo.builder()
                        .guide(info.getGuide())
                        .imageUrl(info.getImageUrl())
                        .orderIndex(info.getOrderIndex())
                        .fileName(info.getFileName())
                        .text(info.getText())
                        .updatedBy(customUserDetails.getId())
                        .product(product)
                        .build())
                .collect(Collectors.toList()));

        product.getProductGuideInfos().addAll(productGuideInfoList);

        List<ProductGuideInfoResponse> productGuideInfoResponses = productGuideInfoList.stream()
                .map(productGuideInfo -> ProductGuideInfoResponse.builder()
                        .guideId(productGuideInfo.getId())
                        .guide(productGuideInfo.getGuide())
                        .orderIndex(productGuideInfo.getOrderIndex())
                        .imageUrl(productGuideInfo.getImageUrl())
                        .fileName(productGuideInfo.getFileName())
                        .text(productGuideInfo.getText())
                        .build()).collect(Collectors.toList());

        return productGuideInfoResponses;
    }

    @Transactional
    public void deleteGuide(Product product){
        productGuideInfoRepository.deleteAllByProduct(product);
    }

}
