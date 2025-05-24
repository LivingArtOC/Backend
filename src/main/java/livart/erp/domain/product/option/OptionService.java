package livart.erp.domain.product.option;

import livart.common.Auth.CustomUserDetails;
import livart.common.domain.product.entity.DetailedOption;
import livart.common.domain.product.entity.Option;
import livart.common.domain.product.entity.OptionMapping;
import livart.common.domain.product.entity.Product;
import livart.common.domain.product.repository.DetailedOptionRepository;
import livart.common.domain.product.repository.OptionMappingRepository;
import livart.common.domain.product.repository.OptionRepository;
import livart.common.domain.product.repository.ProductRepository;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.service.GlobalService;
import livart.erp.domain.product.option.OptionRequest;
import livart.erp.domain.product.option.DetailedOptionResponse;
import livart.erp.domain.product.option.OptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OptionService {
    private final GlobalService globalService;
    private final OptionRepository optionRepository;
    private final DetailedOptionRepository detailedOptionRepository;
    private final OptionMappingRepository optionMappingRepository;

    @Transactional
    public OptionListResponse saveOption(CustomUserDetails customUserDetails, Product savedProduct, List<OptionCombinationRequest> comRequest, List<DetailedOptionRequest> detRequest){
        globalService.validateAdmin(customUserDetails);

        List<DetailedOptionRequest> detailedOptionRequests = detRequest.stream()
                .sorted(Comparator.comparingInt(DetailedOptionRequest::getOrderIndex))
                .collect(Collectors.toList());

        List<DetailedOption> detailedOptions = detailedOptionRepository.saveAll(detailedOptionRequests.stream()
                .map(d -> DetailedOption.builder()
                        .orderIndex(d.getOrderIndex())
                        .optionName(d.getOptionName())
                        .valueName(d.getValueName())
                        .imageUrl(d.getImageUrl())
                        .fileName(d.getFileName())
                        .updatedBy(customUserDetails.getId())
                        .product(savedProduct)
                        .build()
                ).collect(Collectors.toList()));

        Map<String, DetailedOption> optionValueMap = detailedOptions.stream()
                .collect(Collectors.toMap(
                        o -> o.getOptionName() + " : " + o.getValueName(),
                        Function.identity()
                ));

        Map<String, Integer> groupOrderMap = detRequest.stream()
                .collect(Collectors.toMap(
                        DetailedOptionRequest::getOptionName,
                        DetailedOptionRequest::getOrderIndex,
                        (existing, replacement) -> existing
                ));

        List<Option> options = comRequest.stream()
                .map(r -> {
                    Option option = Option.builder()
                            .optionCode(r.getOptionCode())
                            .isExposed(r.getIsExposed())
                            .status(r.getStatus())
                            .price(r.getPrice())
                            .purchasePrice(r.getPurchasePrice())
                            .product(savedProduct)
                            .updatedBy(customUserDetails.getId())
                            .build();

                    String hashedKey = r.getOptionRequestList().stream()
                            .sorted(Comparator.comparing(o -> groupOrderMap.get(o.getOptionName())))  // 그룹 순서 기준 정렬
                            .map(o -> o.getOptionName() + ":" + o.getValueName())
                            .collect(Collectors.joining("_"));

                    List<OptionMapping> mappings = r.getOptionRequestList().stream()
                            .sorted(Comparator.comparing(o -> groupOrderMap.get(o.getOptionName())))
                            .map(m -> {
                                String key = m.getOptionName() + " : " + m.getValueName();
                                DetailedOption detailedOption = optionValueMap.get(key);

                                if(detailedOption == null){
                                    throw new CustomException(ErrorCode.DETAILED_OPTION_NOT_FOUND);
                                }

                                OptionMapping mapping = OptionMapping.builder()
                                        .option(option)
                                        .detailedOption(detailedOption)
                                        .updatedBy(customUserDetails.getId())
                                        .build();

                                detailedOption.getOptionMappings().add(mapping);
                                return mapping;

                            }).collect(Collectors.toList());

                    option.getOptionMappings().addAll(mappings);
                    option.setHashCode(hashedKey);
                    return option;
                }).collect(Collectors.toList());

        List<Option> optionList = optionRepository.saveAll(options);
        savedProduct.getOptions().addAll(optionList);

        return toDto(savedProduct);

    }

    public OptionListResponse getOption(CustomUserDetails customUserDetails, Product product){
        globalService.validateAdmin(customUserDetails);

        return toDto(product);
    }

    @Transactional
    public void deleteOption(Product product){
        List<Option> options = optionRepository.findAllByProductId(product.getId());
        optionMappingRepository.deleteByOptionIn(options);
        detailedOptionRepository.deleteAllByProductId(product.getId());
        optionRepository.deleteAllInBatch(options);
    }

    @Transactional
    public OptionListResponse updateOption(CustomUserDetails customUserDetails, Product product, List<OptionCombinationRequest> comRequest, List<DetailedOptionRequest> detRequest){
        globalService.validateAdmin(customUserDetails);

        deleteOption(product);

        return saveOption(customUserDetails, product, comRequest, detRequest);
    }

    private OptionListResponse toDto(Product product){
        List<DetailedOptionResponse> detailedOptionResponses = detailedOptionRepository.findByProductIdOrderByOrderIndexAsc(product.getId()).stream()
                .map(d -> DetailedOptionResponse.builder()
                        .detailOptionId(d.getId())
                        .optionName(d.getOptionName())
                        .valueName(d.getValueName())
                        .fileName(d.getFileName())
                        .imageUrl(d.getImageUrl())
                        .build()
                ).collect(Collectors.toList());

        List<OptionCombinationResponse> combinationResponses = optionRepository.findAllWithMappingsByProductId(product.getId()).stream()
                .map(o -> {
                    List<OptionResponse> optionResponseList = o.getOptionMappings().stream()
                            .map(m -> OptionResponse.builder()
                                    .detailedOptionId(m.getDetailedOption().getId())
                                    .optionName(m.getDetailedOption().getOptionName())
                                    .valueName(m.getDetailedOption().getValueName())
                                    .build()
                            ).collect(Collectors.toList());

                    return OptionCombinationResponse.builder()
                            .optionId(o.getId())
                            .purchasePrice(o.getPurchasePrice())
                            .price(o.getPrice())
                            .optionCode(o.getOptionCode())
                            .isExposed(o.getIsExposed())
                            .status(o.getStatus())
                            .optionResponseList(optionResponseList)
                            .build();}
                ).collect(Collectors.toList());

        return OptionListResponse.builder()
                .detailedOptionResponse(detailedOptionResponses)
                .combinationResponse(combinationResponses)
                .build();
    }

    public List<OptionAddResponse> getOptionsForProduct(Product product) {

        return optionRepository.findAllWithMappingsByProductId(product.getId()).stream()
                .map(o -> {
                    List<OptionResponse> optionResponseList = o.getOptionMappings().stream()
                            .map(m -> OptionResponse.builder()
                                    .detailedOptionId(m.getDetailedOption().getId())
                                    .optionName(m.getDetailedOption().getOptionName())
                                    .valueName(m.getDetailedOption().getValueName())
                                    .build()
                            ).collect(Collectors.toList());

                    return OptionAddResponse.builder()
                            .optionId(o.getId())
                            .hashCode(o.getHashCode())
                            .purchasePrice(o.getPurchasePrice())
                            .price(o.getPrice())
                            .optionCode(o.getOptionCode())
                            .isExposed(o.getIsExposed())
                            .status(o.getStatus())
                            .optionResponseList(optionResponseList)
                            .build();}
                ).collect(Collectors.toList());
    }
}
