package livart.erp.domain.support.estimate;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import livart.common.Auth.CustomUserDetails;
import livart.common.domain.product.entity.*;
import livart.common.domain.product.repository.OptionRepository;
import livart.common.domain.product.repository.ProductRepository;
import livart.common.domain.support.estimate.entity.*;
import livart.common.domain.support.estimate.repository.EstimateFileRepository;
import livart.common.domain.support.estimate.repository.EstimateItemOptionRepository;
import livart.common.domain.support.estimate.repository.EstimateItemRepository;
import livart.common.domain.support.estimate.repository.EstimateRepository;
import livart.common.domain.user.entity.QUser;
import livart.common.dto.enums.estimate.EstimateStatus;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.mapper.SearchResult;
import livart.common.service.GlobalService;
import livart.common.util.QuerydslSortUtil;
import livart.erp.domain.support.estimate.dto.request.EstimateProductRequest;
import livart.erp.domain.support.estimate.dto.request.EstimateSearchRequest;
import livart.erp.domain.support.estimate.dto.request.EstimateUpdateRequest;
import livart.erp.domain.support.estimate.dto.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstimateService {
    private final GlobalService globalService;
    private final EstimateRepository estimateRepository;
    private final EstimateFileRepository estimateFileRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final EstimateItemRepository estimateItemRepository;
    private final EstimateItemOptionRepository estimateItemOptionRepository;

    public SearchResult<EstimateSearchResponse> searchEstimateList(CustomUserDetails customUserDetails, EstimateSearchRequest request, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        QEstimate estimate = QEstimate.estimate;
        BooleanBuilder builder = new BooleanBuilder();
        
        if(StringUtils.hasText(request.getKeyword()) && request.getKey() != null){
            switch (request.getKey()){
                case PROPOSER -> builder.and(estimate.proposer.containsIgnoreCase(request.getKeyword()));
                case COMPANY_NAME -> builder.and(estimate.companyName.containsIgnoreCase(request.getKeyword()));
                case MANAGER_NAME -> builder.and(estimate.managerName.containsIgnoreCase(request.getKeyword()));
                case EMAIL -> builder.and(estimate.email.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keywordBuilder = new BooleanBuilder();
                    keywordBuilder.and(estimate.proposer.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.and(estimate.companyName.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.and(estimate.managerName.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.and(estimate.email.containsIgnoreCase(request.getKeyword()));
                    builder.and(keywordBuilder);
                }
            }
        }

        if(request.getStatus() != null && request.getStatus() != EstimateStatus.ALL){
            builder.and(estimate.status.eq(request.getStatus()));
        }

        if(request.getApplyDate() != null){
            if(request.getApplyDate().getStartDate() != null){
                builder.and(estimate.createdAt.goe(request.getApplyDate().getStartDate().atStartOfDay()));
            }

            if(request.getApplyDate().getEndDate() != null){
                builder.and(estimate.createdAt.loe(request.getApplyDate().getEndDate().atTime(23,59,59)));
            }
        }

        OrderSpecifier<?>[] orderSpecifiers = QuerydslSortUtil.getOrderSpecifiers(pageable, Estimate.class, "estimate");

        List<Estimate> estimates = jpaQueryFactory
                .selectFrom(estimate)
                .where(builder)
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long countResult = jpaQueryFactory
                .select(estimate.count())
                .from(estimate)
                .where(builder)
                .fetchOne();

        long totalCount = (countResult != null) ? countResult : 0L;

        List<EstimateSearchResponse> responses = estimates.stream()
                .map(est -> EstimateSearchResponse.builder()
                                    .estimateId(est.getId())
                                    .proposer(est.getProposer())
                                    .companyName(est.getCompanyName())
                                    .managerName(est.getManagerName())
                                    .phoneNum(est.getPhoneNum())
                                    .email(est.getEmail())
                                    .status(est.getStatus())
                                    .build()
                ).collect(Collectors.toList());

        return SearchResult.<EstimateSearchResponse>builder()
                .totalCount(totalCount)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();
    }

    public EstimateResponse getEstimate(CustomUserDetails customUserDetails , Long estimateId) {
        globalService.validateAdmin(customUserDetails);

        QEstimate estimate = QEstimate.estimate;
        QEstimateItem item = QEstimateItem.estimateItem;

        Estimate est = jpaQueryFactory.selectFrom(estimate)
                .leftJoin(estimate.estimateItems, item).fetchJoin()
                .where(estimate.id.eq(estimateId))
                .distinct()
                .fetchOne();

        if (est == null) throw new CustomException(ErrorCode.ESTIMATE_NOT_FOUND);

        List<EstimateFile> files = estimateFileRepository.findByEstimateId(estimateId);

        List<FileInfoResponse> referenceImgList = new ArrayList<>();
        List<FileInfoResponse> spaceImgList = new ArrayList<>();
        List<FileInfoResponse> attachFileList = new ArrayList<>();
        for (EstimateFile f : files) {
            switch (f.getType()) {
                case REFERENCE -> referenceImgList.add(toFileResponse(f));
                case SPACE -> spaceImgList.add(toFileResponse(f));
                case ATTACH -> attachFileList.add(toFileResponse(f));
            }
        }

        BooleanBuilder optionBuilder = new BooleanBuilder();

        for (EstimateItem it : est.getEstimateItems()) {
            optionBuilder.or(QOption.option.product.id.eq(it.getProductId())
                    .and(QOption.option.hashCode.eq(it.getHashCode())));
        }

        QOption option = QOption.option;
        QProduct product = QProduct.product;

        List<Option> optionList = jpaQueryFactory
                .selectFrom(option)
                .join(option.product, product).fetchJoin()
                .where(optionBuilder)
                .fetch();

        Map<String, Option> allOptionMap = optionList.stream()
                .collect(Collectors.toMap(
                        op -> op.getProduct().getId() + "_" + op.getHashCode(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        List<Long> itemIds = est.getEstimateItems().stream()
                .map(EstimateItem::getId)
                .toList();

        QEstimateItemOption itemOption = QEstimateItemOption.estimateItemOption;

        List<EstimateItemOption> itemOptionList = jpaQueryFactory
                .selectFrom(itemOption)
                .where(itemOption.estimateItem.id.in(itemIds))
                .fetch();

        Map<Long, List<EstimateItemOption>> itemOptionMap = itemOptionList.stream()
                .collect(Collectors.groupingBy(io -> io.getEstimateItem().getId()));

        List<EstimateProductResponse> productList = est.getEstimateItems().stream()
                .map(i -> toEstProductDto(i, itemOptionMap.getOrDefault(i.getId(), List.of()), allOptionMap))
                .collect(Collectors.toList());

        return EstimateResponse.builder()
                .estimateId(est.getId())
                .proposer(est.getProposer())
                .companyName(est.getCompanyName())
                .managerName(est.getManagerName())
                .phoneNum(est.getPhoneNum())
                .email(est.getEmail())
                .preferVisitDate(est.getVisitDate())
                .preferDeliveryDate(est.getDeliveryDate())
                .emailAlarm(est.getEmailAlarm())
                .kakaoAlarm(est.getKakaoAlarm())
                .referenceImgList(referenceImgList)
                .spaceImgList(spaceImgList)
                .attachFileList(attachFileList)
                .productList(productList)
                .content(est.getContent())
                .isAgreed(est.getIsAgreed())
                .status(est.getStatus())
                .memo(est.getMemo())
                .build();
    }


    private FileInfoResponse toFileResponse(EstimateFile file) {
        return FileInfoResponse.builder()
                .fileId(file.getId())
                .type(file.getType())
                .fileUrl(file.getFileUrl())
                .fileName(file.getFileName())
                .build();
    }

    private EstimateProductResponse toEstProductDto(
            EstimateItem item,
            List<EstimateItemOption> optionList,
            Map<String, Option> optionMap
    ) {
        String key = item.getProductId() + "_" + item.getHashCode();
        Option option = optionMap.get(key);

        List<EstimateOptionResponse> options = optionList.stream()
                .map(e -> EstimateOptionResponse.builder()
                        .estimateOptionId(e.getId())
                        .optionName(e.getOptionName())
                        .valueName(e.getValueName())
                        .build()
                ).collect(Collectors.toList());

        BigDecimal salePrice;
        BigDecimal originalPrice;
        String message;

        if (option != null) {
            salePrice = option.getProduct().getSalePrice()
                    .add(option.getPrice())
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            originalPrice = option.getProduct().getOriginalPrice()
                    .add(option.getPrice())
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            message = null;
        } else {
            log.warn("Option not found for productId: {}, hashCode: {}", item.getProductId(), item.getHashCode());
            salePrice = null;
            originalPrice = null;
            message = "해당 제품의 옵션 종류가 변경되었습니다.";
        }

        return EstimateProductResponse.builder()
                .itemId(item.getId())
                .productName(item.getProductName())
                .thumbNailImgUrl(item.getThumbNailImgUrl())
                .brand(item.getBrand())
                .quantity(item.getQuantity())
                .originalPrice(originalPrice)
                .salePrice(salePrice)
                .message(message)
                .options(options)
                .build();
    }


    @Transactional
    public EstimateResponse updateEstimate(CustomUserDetails customUserDetails , Long estimateId, EstimateUpdateRequest request){
        globalService.validateAdmin(customUserDetails);

        Estimate estimate = estimateRepository.findById(estimateId)
                .orElseThrow(() -> new CustomException(ErrorCode.ESTIMATE_NOT_FOUND));

        estimateItemOptionRepository.deleteOptionsByEstimateId(estimateId);
        estimateItemRepository.deleteAllByEstimateId(estimateId);

        estimate.update(request.getCompanyName(), request.getManagerName(), request.getPhoneNum(), request.getVisitDate(), request.getDeliveryDate(), request.getEmail(), request.getMemo(), request.getStatus() , customUserDetails.getId());

        QOption o = QOption.option;
        QProduct p = QProduct.product;
        QOptionMapping m = QOptionMapping.optionMapping;
        QDetailedOption d = QDetailedOption.detailedOption;

        BooleanBuilder builder = new BooleanBuilder();
        for (EstimateProductRequest req : request.getProductRequests()) {
            builder.or(o.product.id.eq(req.getProductId())
                    .and(o.hashCode.eq(req.getHashCode())));
        }

        List<Option> options = jpaQueryFactory.selectFrom(o)
                .join(o.product, p).fetchJoin()
                .leftJoin(o.optionMappings, m).fetchJoin()
                .leftJoin(m.detailedOption, d).fetchJoin()
                .where(builder)
                .fetch();

        Map<String, Option> optionMap = options.stream()
                .collect(Collectors.toMap(
                        opt -> opt.getProduct().getId() + "_" + opt.getHashCode(),
                        Function.identity()
                ));

        List<EstimateItem> newItems = request.getProductRequests().stream()
                .map(req -> {
                    String key = req.getProductId() + "_" + req.getHashCode();
                    Option opt = optionMap.get(key);

                    if (opt == null) {
                        throw new CustomException(ErrorCode.OPTION_NOT_FOUND);
                    }

                    EstimateItem item = EstimateItem.builder()
                            .productName(opt.getProduct().getProductName())
                            .thumbNailImgUrl(req.getThumbNailImgUrl())
                            .optionCode(req.getOptionCode())
                            .brand(opt.getProduct().getBrand())
                            .quantity(req.getQuantity())
                            .productId(req.getProductId())
                            .hashCode(req.getHashCode())
                            .updatedBy(customUserDetails.getId())
                            .estimate(estimate)
                            .build();

                    List<EstimateItemOption> itemOptions = opt.getOptionMappings().stream()
                            .map(mapping -> EstimateItemOption.builder()
                                    .optionName(mapping.getDetailedOption().getOptionName())
                                    .valueName(mapping.getDetailedOption().getValueName())
                                    .imageUrl(mapping.getOption().getImageUrl())
                                    .fileName(mapping.getOption().getFileName())
                                    .updatedBy(customUserDetails.getId())
                                    .estimateItem(item)
                                    .build())
                            .collect(Collectors.toList());

                    item.getEstimateItemOptions().addAll(itemOptions);
                    return item;
                }).collect(Collectors.toList());

        estimate.getEstimateItems().addAll(newItems);
        // 7. 저장
        Estimate saved = estimateRepository.save(estimate);

        QEstimate qEstimate = QEstimate.estimate;
        QEstimateItem item = QEstimateItem.estimateItem;
        QEstimateItemOption itemOption = QEstimateItemOption.estimateItemOption;

        Estimate est = jpaQueryFactory.selectFrom(qEstimate)
                .leftJoin(qEstimate.estimateItems, item).fetchJoin()
                .where(qEstimate.id.eq(saved.getId()))
                .distinct()
                .fetchOne();

        if (est == null) throw new CustomException(ErrorCode.ESTIMATE_NOT_FOUND);

        List<EstimateFile> files = estimateFileRepository.findByEstimateId(estimateId);

        List<FileInfoResponse> referenceImgList = new ArrayList<>();
        List<FileInfoResponse> spaceImgList = new ArrayList<>();
        List<FileInfoResponse> attachFileList = new ArrayList<>();
        for (EstimateFile f : files) {
            switch (f.getType()) {
                case REFERENCE -> referenceImgList.add(toFileResponse(f));
                case SPACE -> spaceImgList.add(toFileResponse(f));
                case ATTACH -> attachFileList.add(toFileResponse(f));
            }
        }

        BooleanBuilder optionBuilder = new BooleanBuilder();
        for (EstimateItem it : est.getEstimateItems()) {
            optionBuilder.or(QOption.option.product.id.eq(it.getProductId())
                    .and(QOption.option.hashCode.eq(it.getHashCode())));
        }

        List<Option> optionList = jpaQueryFactory
                .selectFrom(QOption.option)
                .join(QOption.option.product, QProduct.product).fetchJoin()
                .where(optionBuilder)
                .fetch();

        Map<String, Option> allOptionMap = optionList.stream()
                .collect(Collectors.toMap(
                        op -> op.getProduct().getId() + "_" + op.getHashCode(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        List<Long> itemIds = est.getEstimateItems().stream()
                .map(EstimateItem::getId)
                .toList();

        List<EstimateItemOption> itemOptionList = jpaQueryFactory
                .selectFrom(itemOption)
                .where(itemOption.estimateItem.id.in(itemIds))
                .fetch();

        Map<Long, List<EstimateItemOption>> itemOptionMap = itemOptionList.stream()
                .collect(Collectors.groupingBy(io -> io.getEstimateItem().getId()));

        List<EstimateProductResponse> productList = est.getEstimateItems().stream()
                .map(i -> toEstProductDto(i, itemOptionMap.getOrDefault(i.getId(), List.of()), allOptionMap))
                .collect(Collectors.toList());

        return EstimateResponse.builder()
                .estimateId(est.getId())
                .proposer(est.getProposer())
                .companyName(est.getCompanyName())
                .managerName(est.getManagerName())
                .phoneNum(est.getPhoneNum())
                .email(est.getEmail())
                .preferVisitDate(est.getVisitDate())
                .preferDeliveryDate(est.getDeliveryDate())
                .emailAlarm(est.getEmailAlarm())
                .kakaoAlarm(est.getKakaoAlarm())
                .referenceImgList(referenceImgList)
                .spaceImgList(spaceImgList)
                .attachFileList(attachFileList)
                .productList(productList)
                .content(est.getContent())
                .isAgreed(est.getIsAgreed())
                .status(est.getStatus())
                .memo(est.getMemo())
                .build();
    }

}
