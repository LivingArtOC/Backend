package livart.erp.domain.support.quotation;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import livart.common.Auth.CustomUserDetails;
import livart.common.domain.product.entity.*;
import livart.common.domain.setting.entity.CompanyInfo;
import livart.common.domain.setting.repository.CompanyInfoRepository;
import livart.common.domain.support.estimate.entity.*;
import livart.common.domain.support.estimate.repository.EstimateRepository;
import livart.common.domain.support.quotation.entity.*;
import livart.common.domain.support.quotation.repository.QuotationItemRepository;
import livart.common.domain.support.quotation.repository.QuotationRepository;
import livart.common.domain.user.repository.AdminRepository;
import livart.common.dto.enums.quotation.QuotationStatus;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.mapper.SearchResult;
import livart.common.service.GlobalService;
import livart.common.dto.request.QuotationRequest;
import livart.erp.domain.support.quotation.dto.request.QuotationSearchRequest;
import livart.erp.domain.support.quotation.dto.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuotationService {
    private final GlobalService globalService;
    private final JPAQueryFactory jpaQueryFactory;
    private final CompanyInfoRepository companyInfoRepository;
    private final QuotationRepository quotationRepository;
    private final EstimateRepository estimateRepository;
    private final AdminRepository adminRepository;
    private final QuotationItemRepository quotationItemRepository;

    public QuotationAllResponse getAllInfo(CustomUserDetails customUserDetails, Long estimateId){
        globalService.validateAdmin(customUserDetails);
        CompanyInfo companyInfo = companyInfoRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.COMPANY_INFO_NOT_FOUND));

        List<PicListResponse> admins = adminRepository.findAll().stream()
                .map(admin -> PicListResponse.builder()
                        .picId(admin.getId())
                        .picName(admin.getAdminName())
                        .picPhoneNum(admin.getPhoneNum())
                        .build()
                ).collect(Collectors.toList());

        QEstimate estimate = QEstimate.estimate;
        QEstimateItem item = QEstimateItem.estimateItem;

        Estimate est = jpaQueryFactory.selectFrom(estimate)
                .leftJoin(estimate.estimateItems, item).fetchJoin()
                .where(estimate.id.eq(estimateId))
                .distinct()
                .fetchOne();

        if (est == null) throw new CustomException(ErrorCode.ESTIMATE_NOT_FOUND);

        QuotationAllResponse response = QuotationAllResponse.builder()
                .estimateId(est.getId())
                .title(null)
                .phoneNum(est.getPhoneNum())
                .bizNum(companyInfo.getBizNum())
                .corporationName(companyInfo.getCompanyName())
                .presidentName(companyInfo.getPresidentName())
                .address(companyInfo.getAddress())
                .picList(admins)
                .detailAddress(companyInfo.getDetailAddress())
                .managerCompanyName(est.getCompanyName())
                .managerName(est.getManagerName())
                .status(QuotationStatus.BEFORE_CREATED)
                .memo(est.getMemo())
                .date(LocalDate.now())
                .build();

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

        QEstimateItemOption itemOption = QEstimateItemOption.estimateItemOption;

        List<EstimateItemOption> itemOptionList = jpaQueryFactory
                .selectFrom(itemOption)
                .where(itemOption.estimateItem.id.in(itemIds))
                .fetch();

        Map<Long, List<EstimateItemOption>> itemOptionMap = itemOptionList.stream()
                .collect(Collectors.groupingBy(io -> io.getEstimateItem().getId()));

        List<QuotationProductResponse> items = est.getEstimateItems().stream()
                .map(e -> {
                    String key = e.getProductId() + "_" + e.getHashCode();
                    Option opt = allOptionMap.get(key);
                    BigDecimal unitOriginalPrice;
                    BigDecimal unitSalePrice;
                    BigDecimal salePriceVat;
                    String message;

                    if(opt != null){
                        unitOriginalPrice = opt.getProduct().getOriginalPrice().add(opt.getPrice());
                        unitSalePrice = opt.getProduct().getSalePrice().add(opt.getPrice());
                        salePriceVat = unitSalePrice.multiply(BigDecimal.valueOf(e.getQuantity()));
                        message = null;
                    }else {
                        log.warn("옵션 해싱 조회 실패 - productId: {}, hashCode: {}", e.getProductId(), e.getHashCode());
                        unitOriginalPrice = null;
                        unitSalePrice = null;
                        salePriceVat = null;
                        message = "해당 제품의 옵션 종류가 추가 혹은 삭제됐습니다.";
                    }

                    List<EstimateItemOption> itemOptions = itemOptionMap.getOrDefault(e.getId(), List.of());

                    List<QuotationOptionResponse> optionResponses = itemOptions.stream()
                                    .map(o -> QuotationOptionResponse.builder()
                                            .optionName(o.getOptionName())
                                            .optionValue(o.getValueName())
                                            .build()
                                    ).collect(Collectors.toList());

                    return QuotationProductResponse.builder()
                            .productId(e.getProductId())
                            .productName(e.getProductName())
                            .productCode(e.getOptionCode())
                            .hashCode(e.getHashCode())
                            .productSize(null)
                            .thumbNailImgUrl(e.getThumbNailImgUrl())
                            .quantity(e.getQuantity())
                            .unitOriginalPrice(unitOriginalPrice)
                            .unitSalePrice(unitSalePrice)
                            .salePriceVat(salePriceVat)
                            .message(message)
                            .note(null)
                            .options(optionResponses)
                            .build();}
                ).collect(Collectors.toList());

        Integer quantitySubtotal = 0; // 수량 소계
        BigDecimal unitPriceSubtotal = BigDecimal.ZERO; // 단가 소계
        BigDecimal priceSubtotalVat = BigDecimal.ZERO; // 공급가액 소계
        BigDecimal discountPrice = BigDecimal.ZERO; // 할인 금액
        BigDecimal unitTruncation = BigDecimal.ZERO; // 할인 금액의 VAT

        for(QuotationProductResponse i : items){
            BigDecimal unitPrice = Optional.ofNullable(i.getUnitSalePrice()).orElse(BigDecimal.ZERO);
            BigDecimal priceVat = Optional.ofNullable(i.getSalePriceVat()).orElse(BigDecimal.ZERO);
            quantitySubtotal = quantitySubtotal + i.getQuantity();
            unitPriceSubtotal = unitPriceSubtotal.add(unitPrice);
            priceSubtotalVat = priceSubtotalVat.add(priceVat);
        }

        BigDecimal totalPriceInclVat = priceSubtotalVat.subtract(discountPrice.add(unitTruncation));
        BigDecimal totalPriceExclVat = totalPriceInclVat.multiply(BigDecimal.valueOf(0.9));

        response.setTotalPriceInclVat(totalPriceInclVat);
        response.setTotalPriceExclVat(totalPriceExclVat);
        response.setQuantitySubtotal(quantitySubtotal);
        response.setUnitPriceSubtotal(unitPriceSubtotal);
        response.setPriceSubtotalVat(priceSubtotalVat);
        response.setDiscountPrice(discountPrice);
        response.setUnitTruncation(unitTruncation);

        response.setProductList(items);

        return response;
    }

    public DefaultInfoResponse getDefault(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        CompanyInfo companyInfo = companyInfoRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.COMPANY_INFO_NOT_FOUND));

        List<PicListResponse> admins = adminRepository.findAll().stream()
                .map(admin -> PicListResponse.builder()
                        .picId(admin.getId())
                        .picName(admin.getAdminName())
                        .picPhoneNum(admin.getPhoneNum())
                        .build()
                ).collect(Collectors.toList());

        return DefaultInfoResponse.builder()
                .bizNum(companyInfo.getBizNum())
                .corporationName(companyInfo.getCompanyName())
                .presidentName(companyInfo.getPresidentName())
                .address(companyInfo.getAddress())
                .picList(admins)
                .detailAddress(companyInfo.getDetailAddress())
                .build();
    }

    @Transactional
    public QuotationResponse registerQuotation(CustomUserDetails customUserDetails, QuotationRequest request){
        globalService.validateAdmin(customUserDetails);

        String title = request.getTitle();

        if(request.getTitle() == null) {
            title = "견적 문의에서 생성된 견적서입니다.";
        }

        String proposer = null;
        Long estimateId = request.getEstimateId();

        if (estimateId != null) {
            Estimate est = estimateRepository.findById(estimateId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ESTIMATE_NOT_FOUND));

            proposer = est.getProposer();
        }

        Quotation quotation = Quotation.builder()
                .type(request.getType())
                .estimateId(request.getEstimateId())
                .phoneNum(request.getPhoneNum())
                .proposer(proposer)
                .author(customUserDetails.getUsername())
                .title(title)
                .bizNum(request.getBizNum())
                .corporationName(request.getCorporationName())
                .presidentName(request.getPresidentName())
                .address(request.getAddress())
                .detailAddress(request.getDetailAddress())
                .picName(request.getPicName())
                .picPhoneNum(request.getPicPhoneNum())
                .status(QuotationStatus.BEFORE_PAYMENT)
                .date(request.getDate())
                .managerCompanyName(request.getManagerCompanyName())
                .managerName(request.getManagerName())
                .memo(request.getMemo())
                .totalPriceExclVat(request.getTotalPriceExclVat())
                .totalPriceInclVat(request.getTotalPriceInclVat())
                .quantitySubtotal(request.getQuantitySubtotal())
                .unitSubtotalVat(request.getUnitPriceSubtotal())
                .priceSubtotal(request.getPriceSubtotalVat())
                .discountPrice(request.getDiscountPrice())
                .unitTruncation(request.getUnitTruncation())
                .createdBy(customUserDetails.getId())
                .build();

        List<QuotationItem> items = request.getProductList().stream()
                .map(p -> {
                    QuotationItem item = QuotationItem.builder()
                            .productId(p.getProductId())
                            .productName(p.getProductName())
                            .hashCode(p.getHashCode())
                            .optionCode(p.getProductCode())
                            .productSize(p.getProductSize())
                            .thumbNailImgUrl(p.getThumbNailImgUrl())
                            .quantity(p.getQuantity())
                            .unitOriginalPrice(p.getUnitOriginalPrice())
                            .unitSalePrice(p.getUnitSalePrice())
                            .salePriceVat(p.getSalePriceVat())
                            .note(p.getNote())
                            .createdBy(customUserDetails.getId())
                            .quotation(quotation)
                            .build();
                    
                    List<QuotationItemOption> options = p.getOptions().stream()
                            .map(o -> QuotationItemOption.builder()
                                    .optionName(o.getOptionName())
                                    .optionValue(o.getOptionValue())
                                    .updatedBy(customUserDetails.getId())
                                    .quotationItem(item)
                                    .build()).collect(Collectors.toList());
                    
                    item.getQuotationItemOptions().addAll(options);
                    
                    return item;
                }).collect(Collectors.toList());
        
        quotation.getQuotationItems().addAll(items);
        
        Quotation saved = quotationRepository.save(quotation);

        return toDto(saved.getId());
        
    }

    public QuotationResponse getQuotation(CustomUserDetails customUserDetails, Long quotationId){
        globalService.validateAdmin(customUserDetails);

        return toDto(quotationId);
    }
    
    private QuotationResponse toDto(Long quotationId){

        QQuotation quotation = QQuotation.quotation;
        QQuotationItem quotationItem = QQuotationItem.quotationItem;
        
        Quotation quo = jpaQueryFactory
                .selectFrom(quotation)
                .leftJoin(quotation.quotationItems, quotationItem).fetchJoin()
                .where(quotation.id.eq(quotationId))
                .distinct()
                .fetchOne();

        if (quo == null) throw new CustomException(ErrorCode.QUOTATION_NOT_FOUND);

        List<Long> itemIds = quo.getQuotationItems().stream()
                .map(QuotationItem::getId)
                .toList();

        QQuotationItemOption itemOption = QQuotationItemOption.quotationItemOption;

        List<QuotationItemOption> itemOptionList = jpaQueryFactory
                .selectFrom(itemOption)
                .where(itemOption.quotationItem.id.in(itemIds))
                .fetch();

        Map<Long, List<QuotationItemOption>> itemOptionMap = itemOptionList.stream()
                .collect(Collectors.groupingBy(io -> io.getQuotationItem().getId()));
        
        List<QuotationProductResponse> productList = quo.getQuotationItems().stream()
                .map(p -> {
                    List<QuotationItemOption> itemOptions = itemOptionMap.getOrDefault(p.getId(), List.of());

                    List<QuotationOptionResponse> options = itemOptions.stream()
                                    .map(o -> QuotationOptionResponse.builder()
                                            .optionName(o.getOptionName())
                                            .optionValue(o.getOptionValue())
                                            .build()
                                    ).collect(Collectors.toList());
                    
                    return QuotationProductResponse.builder()
                            .itemId(p.getId())
                            .productId(p.getProductId())
                            .productSize(p.getProductSize())
                            .productCode(p.getOptionCode())
                            .productName(p.getProductName())
                            .hashCode(p.getHashCode())
                            .thumbNailImgUrl(p.getThumbNailImgUrl())
                            .quantity(p.getQuantity())
                            .unitOriginalPrice(p.getUnitOriginalPrice())
                            .unitSalePrice(p.getUnitSalePrice())
                            .salePriceVat(p.getSalePriceVat())
                            .note(p.getNote())
                            .options(options)
                            .build();
                }).collect(Collectors.toList());
        
        return QuotationResponse.builder()
                .quotationId(quo.getId())
                .estimateId(quo.getEstimateId())
                .title(quo.getTitle())
                .phoneNum(quo.getPhoneNum())
                .bizNum(quo.getBizNum())
                .corporationName(quo.getCorporationName())
                .presidentName(quo.getPresidentName())
                .address(quo.getAddress())
                .detailAddress(quo.getDetailAddress())
                .picName(quo.getPicName())
                .picPhoneNum(quo.getPicPhoneNum())
                .status(QuotationStatus.BEFORE_PAYMENT)
                .date(quo.getDate())
                .managerCompanyName(quo.getManagerCompanyName())
                .managerName(quo.getManagerName())
                .memo(quo.getMemo())
                .totalPriceExclVat(quo.getTotalPriceExclVat())
                .totalPriceInclVat(quo.getTotalPriceInclVat())
                .quantitySubtotal(quo.getQuantitySubtotal())
                .unitSubtotalVat(quo.getUnitSubtotalVat())
                .priceSubtotal(quo.getPriceSubtotal())
                .totalDiscount(quo.getDiscountPrice())
                .unitTruncation(quo.getUnitTruncation())
                .productList(productList)
                .build();

    }

    @Transactional
    public QuotationResponse updateQuotation(CustomUserDetails customUserDetails, Long quotationId, QuotationRequest request){
        globalService.validateAdmin(customUserDetails);

        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUOTATION_NOT_FOUND));

        List<QuotationItem> items = quotationItemRepository.findAllByQuotation(quotation);
        quotationItemRepository.deleteAll(items);

        String title = request.getTitle();

        if(request.getTitle() == null) {
            title = "견적 문의에서 생성된 견적서입니다.";
        }

        String proposer = null;
        Long estimateId = request.getEstimateId();

        if (estimateId != null) {
            Estimate est = estimateRepository.findById(estimateId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ESTIMATE_NOT_FOUND));

            proposer = est.getProposer();
        }

        quotation.update(request, customUserDetails.getId(), proposer, title, customUserDetails.getUsername());

        List<QuotationItem> itemList = request.getProductList().stream()
                .map(p -> {
                    QuotationItem item = QuotationItem.builder()
                            .productId(p.getProductId())
                            .productName(p.getProductName())
                            .hashCode(p.getHashCode())
                            .optionCode(p.getProductCode())
                            .productSize(p.getProductSize())
                            .thumbNailImgUrl(p.getThumbNailImgUrl())
                            .quantity(p.getQuantity())
                            .unitOriginalPrice(p.getUnitOriginalPrice())
                            .unitSalePrice(p.getUnitSalePrice())
                            .salePriceVat(p.getSalePriceVat())
                            .note(p.getNote())
                            .createdBy(customUserDetails.getId())
                            .quotation(quotation)
                            .build();

                    List<QuotationItemOption> options = p.getOptions().stream()
                            .map(o -> QuotationItemOption.builder()
                                    .optionName(o.getOptionName())
                                    .optionValue(o.getOptionValue())
                                    .updatedBy(customUserDetails.getId())
                                    .quotationItem(item)
                                    .build()).collect(Collectors.toList());

                    item.getQuotationItemOptions().addAll(options);

                    return item;
                }).collect(Collectors.toList());

        quotation.getQuotationItems().addAll(itemList);
        Quotation saved = quotationRepository.save(quotation);

        return toDto(saved.getId());
    }

    public SearchResult<QuotationSearchResponse> searchQuotation(CustomUserDetails customUserDetails, QuotationSearchRequest request, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        QQuotation qQuotation = QQuotation.quotation;
        BooleanBuilder builder = new BooleanBuilder();

        if(StringUtils.hasText(request.getKeyword()) && request.getKey() != null){
            switch (request.getKey()){
                case AUTHOR -> builder.and(qQuotation.author.containsIgnoreCase(request.getKeyword()));
                case PROPOSER -> builder.and(qQuotation.proposer.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder allBuilder = new BooleanBuilder();
                    allBuilder.or(qQuotation.author.containsIgnoreCase(request.getKeyword()));
                    allBuilder.or(qQuotation.proposer.containsIgnoreCase(request.getKeyword()));
                    allBuilder.or(qQuotation.managerName.containsIgnoreCase(request.getKeyword()));
                    builder.and(allBuilder);
                }
            }
        }

        if(request.getStatus() != null  && request.getStatus() == QuotationStatus.ALL){
            builder.and(qQuotation.status.eq(request.getStatus()));
        }

        if(request.getRegisterDate() != null){
            if(request.getRegisterDate().getStartDate() != null){
                builder.and(qQuotation.createdAt.goe(request.getRegisterDate().getStartDate().atStartOfDay()));
            }

            if(request.getRegisterDate().getEndDate() != null){
                builder.and(qQuotation.createdAt.loe(request.getRegisterDate().getEndDate().atTime(23,59,59)));
            }
        }

        List<Quotation> quotation = jpaQueryFactory
                .selectFrom(qQuotation)
                .where(builder)
                .orderBy(qQuotation.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long countResult = jpaQueryFactory
                .select(qQuotation.count())
                .from(qQuotation)
                .where(builder)
                .fetchOne();

        long totalCount = (countResult != null) ? countResult : 0L;

        List<QuotationSearchResponse> responses = quotation.stream()
                .map(q -> QuotationSearchResponse.builder()
                        .quotationId(q.getId())
                        .title(q.getTitle())
                        .picName(q.getPicName())
                        .picPhoneNum(q.getPicPhoneNum())
                        .proposer(q.getProposer())
                        .registerDate(q.getCreatedAt().toLocalDate())
                        .status(q.getStatus())
                        .build()).collect(Collectors.toList());

        return SearchResult.<QuotationSearchResponse>builder()
                .totalCount(totalCount)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();
    }

    @Transactional
    public void deleteQuotation(CustomUserDetails customUserDetails, List<Long> idList){
        globalService.validateAdmin(customUserDetails);

        List<Quotation> quotations = quotationRepository.findAllById(idList);

        quotationRepository.deleteAll(quotations);
    }


}
