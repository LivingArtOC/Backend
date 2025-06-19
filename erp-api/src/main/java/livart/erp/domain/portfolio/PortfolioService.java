package livart.erp.domain.portfolio;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import livart.common.Auth.CustomUserDetails;
import livart.common.domain.order.entity.Order;
import livart.common.domain.order.repository.OrderRepository;
import livart.common.domain.portfolio.entity.*;
import livart.common.domain.portfolio.repository.*;
import livart.common.domain.product.entity.DetailedOption;
import livart.common.domain.product.repository.DetailedOptionRepository;
import livart.common.dto.enums.portfolio.ImageType;
import livart.common.dto.enums.portfolio.PortfolioStatus;
import livart.common.dto.request.DateSearchDto;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.mapper.SearchResult;
import livart.common.service.GlobalService;
import livart.erp.domain.portfolio.dto.request.*;
import livart.erp.domain.portfolio.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioService {
    private final GlobalService globalService;
    private final PortfolioRepository portfolioRepository;
    private final PortfolioImageRepository portfolioImageRepository;
    private final PortfolioItemOptionRepository portfolioItemOptionRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final PortfolioDisplayRepository portfolioDisplayRepository;

    @Transactional
    public PfResponse registerPf(CustomUserDetails customUserDetails, PfRegisterRequest request){
        globalService.validateAdmin(customUserDetails);

        Portfolio portfolio = Portfolio.builder()
                .companyName(request.getCompanyName())
                .location(request.getLocation())
                .concept(request.getConcept())
                .status(PortfolioStatus.TEMPORARY_STORAGE)
                .description(request.getDescription())
                .registerStartDate(request.getRegisterDate().getStartDate())
                .registerEndDate(request.getRegisterDate().getEndDate())
                .createdBy(customUserDetails.getId())
                .build();

        List<PortfolioItem> portfolioItems = request.getItemList().stream()
                .map(r -> {
                    PortfolioItem pf = PortfolioItem.builder()
                                    .orderIndex(r.getOrderIndex())
                                    .productName(r.getProductName())
                                    .productImageUrl(r.getProductImageUrl())
                                    .brandType(r.getBrandType())
                                    .updatedBy(customUserDetails.getId())
                                    .portfolio(portfolio)
                                    .build();
                    List<PortfolioItemOption> options = r.getOptionList().stream()
                            .map(o -> PortfolioItemOption.builder()
                                    .valueName(o.getValueName())
                                    .optionName(o.getOptionName())
                                    .updatedBy(customUserDetails.getId())
                                    .portfolioItem(pf)
                                    .build()
                            ).collect(Collectors.toList());

                    pf.getPortfolioItemOptions().addAll(options);

                    return pf;
                }).collect(Collectors.toList());


        if (request.getImageList() != null) {
            List<PortfolioImage> images = request.getImageList().stream()
                    .map(img -> PortfolioImage.builder()
                            .portfolio(portfolio)
                            .imageType(img.getImageType())
                            .fileName(img.getFileName())
                            .imageUrl(img.getImageUrl())
                            .orderIndex(img.getOrderIndex())
                            .detailComment(img.getDetailComment())
                            .updatedBy(customUserDetails.getId())
                            .build()
                    ).collect(Collectors.toList());

            portfolio.getPortfolioImages().addAll(images);
        }
        portfolio.getPortfolioItems().addAll(portfolioItems);
        Portfolio saved = portfolioRepository.save(portfolio);

        return toPortfolioResponse(saved);

    }

    public PfResponse getPf(CustomUserDetails customUserDetails, Long portfolioId){
        globalService.validateAdmin(customUserDetails);

        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new CustomException(ErrorCode.PORTFOLIO_NOT_FOUND));

        return toPortfolioResponse(portfolio);
    }

    @Transactional
    public PfResponse updatePf(CustomUserDetails customUserDetails, Long portfolioId, PfRegisterRequest request){
        globalService.validateAdmin(customUserDetails);

        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new CustomException(ErrorCode.PORTFOLIO_NOT_FOUND));

        portfolioItemOptionRepository.deleteOptionsByPortfolioId(portfolioId);
        portfolioItemRepository.deleteItemsByPortfolioId(portfolioId);
        portfolioImageRepository.deleteImagesByPortfolioId(portfolioId);

        portfolio.update(request.getCompanyName(), request.getLocation(), request.getConcept(), request.getDescription(), request.getRegisterDate().getStartDate(), request.getRegisterDate().getEndDate(), customUserDetails.getId());

        List<PortfolioItem> portfolioItems = request.getItemList().stream()
                .map(r -> {
                    PortfolioItem pf = PortfolioItem.builder()
                            .orderIndex(r.getOrderIndex())
                            .productName(r.getProductName())
                            .productImageUrl(r.getProductImageUrl())
                            .brandType(r.getBrandType())
                            .updatedBy(customUserDetails.getId())
                            .portfolio(portfolio)
                            .build();
                    List<PortfolioItemOption> options = r.getOptionList().stream()
                            .map(o -> PortfolioItemOption.builder()
                                    .valueName(o.getValueName())
                                    .optionName(o.getOptionName())
                                    .updatedBy(customUserDetails.getId())
                                    .portfolioItem(pf)
                                    .build()
                            ).collect(Collectors.toList());

                    pf.getPortfolioItemOptions().addAll(options);

                    return pf;
                }).collect(Collectors.toList());


        if (request.getImageList() != null) {
            List<PortfolioImage> images = request.getImageList().stream()
                    .map(img -> PortfolioImage.builder()
                            .portfolio(portfolio)
                            .imageType(img.getImageType())
                            .fileName(img.getFileName())
                            .imageUrl(img.getImageUrl())
                            .orderIndex(img.getOrderIndex())
                            .detailComment(img.getDetailComment())
                            .updatedBy(customUserDetails.getId())
                            .build()
                    ).collect(Collectors.toList());

            portfolio.getPortfolioImages().addAll(images);
        }
        portfolio.getPortfolioItems().addAll(portfolioItems);
        Portfolio saved = portfolioRepository.save(portfolio);

        return toPortfolioResponse(saved);
    }

    private PfResponse toPortfolioResponse(Portfolio portfolio) {

        List<PfImageResponse> imageResponses = portfolioImageRepository.findAllByPortfolio(portfolio).stream()
                .map(img -> PfImageResponse.builder()
                        .imageId(img.getId())
                        .imageType(img.getImageType())
                        .fileName(img.getFileName())
                        .imageUrl(img.getImageUrl())
                        .orderIndex(img.getOrderIndex())
                        .detailComment(img.getDetailComment())
                        .build()
                ).collect(Collectors.toList());

        List<PortfolioItem> itemList = portfolioItemRepository.findAllByPortfolio(portfolio);

        List<Long> itemIds = itemList.stream().map(PortfolioItem::getId).toList();
        List<PortfolioItemOption> optionList = portfolioItemOptionRepository.findByPortfolioItemIdIn(itemIds);

        Map<Long, List<PortfolioItemOption>> optionMap = optionList.stream()
                .collect(Collectors.groupingBy(opt -> opt.getPortfolioItem().getId()));

        List<PfItemResponse> itemResponses = itemList.stream()
                .map(item -> {
                    List<PfOptionResponse> optionResponses = optionMap.getOrDefault(item.getId(), List.of())
                            .stream()
                            .map(opt -> PfOptionResponse.builder()
                                    .optionId(opt.getId())
                                    .optionName(opt.getOptionName())
                                    .optionValue(opt.getValueName())
                                    .build()
                            ).collect(Collectors.toList());

                    return PfItemResponse.builder()
                            .itemId(item.getId())
                            .productName(item.getProductName())
                            .productImageUrl(item.getProductImageUrl())
                            .brandType(item.getBrandType())
                            .orderIndex(item.getOrderIndex())
                            .optionList(optionResponses)
                            .build();
                })
                .sorted(Comparator.comparing(
                        PfItemResponse::getOrderIndex,
                        Comparator.nullsLast(Integer::compareTo))
                ).collect(Collectors.toList());

        DateSearchDto date = DateSearchDto.builder()
                .startDate(portfolio.getRegisterStartDate())
                .endDate(portfolio.getRegisterEndDate())
                .build();

        return PfResponse.builder()
                .portfolioId(portfolio.getId())
                .companyName(portfolio.getCompanyName())
                .location(portfolio.getLocation())
                .concept(portfolio.getConcept())
                .description(portfolio.getDescription())
                .registerDate(date)
                .imageList(imageResponses)
                .itemList(itemResponses)
                .build();
    }

    @Transactional
    public List<PfStatusResponse> updateStatusPf(CustomUserDetails customUserDetails, PfStatusRequest request){
        globalService.validateAdmin(customUserDetails);

        List<Portfolio> portfolios = portfolioRepository.findAllById(request.getIdList());

        if(!(portfolios.size() == request.getIdList().size())){
            throw new CustomException(ErrorCode.PORTFOLIO_NOT_FOUND);
        }

        return portfolios.stream()
                .map(p -> {
                    p.updateStatus(request.getUpdateStatus(), customUserDetails.getId());

                    return PfStatusResponse.builder()
                            .portfolioId(p.getId())
                            .companyName(p.getCompanyName())
                            .status(p.getStatus())
                            .description(p.getDescription())
                            .location(p.getLocation())
                            .concept(p.getConcept())
                            .startDate(p.getRegisterStartDate())
                            .endDate(p.getRegisterEndDate())
                            .build();
                }).collect(Collectors.toList());
    }

    public SearchResult<PfSearchResponse> searchPf(CustomUserDetails customUserDetails, PfSearchRequest request, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        QPortfolio portfolio = QPortfolio.portfolio;
        QPortfolioImage portfolioImage = QPortfolioImage.portfolioImage;
        BooleanBuilder builder = new BooleanBuilder()
                .and(portfolio.status.ne(PortfolioStatus.DELETED));

        if (StringUtils.hasText(request.getKeyword()) && request.getKey() != null) {
            switch (request.getKey()) {
                case COMPANY_NAME -> builder.and(portfolio.companyName.containsIgnoreCase(request.getKeyword()));
                case CONCEPT -> builder.and(portfolio.concept.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keywordBuilder = new BooleanBuilder();
                    keywordBuilder.or(portfolio.companyName.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(portfolio.concept.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(portfolio.location.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(portfolio.description.containsIgnoreCase(request.getKeyword()));
                    builder.and(keywordBuilder);
                }
            }
        }

        if (request.getRegisterDate() != null) {
            if (request.getRegisterDate().getStartDate() != null) {
                builder.and(portfolio.registerStartDate.goe(request.getRegisterDate().getStartDate()));
            }
            if (request.getRegisterDate().getEndDate() != null) {
                builder.and(portfolio.registerEndDate.loe(request.getRegisterDate().getEndDate()));
            }
        }

        List<Long> portfolioId = jpaQueryFactory
                .select(portfolio.id)
                .from(portfolio)
                .leftJoin(portfolio.portfolioImages, portfolioImage)
                .on(portfolioImage.imageType.eq(ImageType.THUMBNAIL))
                .where(builder)
                .orderBy(portfolioImage.createdAt.desc()) // 정렬 필수!
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (portfolioId.isEmpty()) {
            return SearchResult.<PfSearchResponse>builder()
                    .totalCount(0L)
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .data(List.of())
                    .build();
        }


        List<Tuple> rows = jpaQueryFactory
                .select(
                        portfolio.id,
                        portfolioImage.imageUrl,
                        portfolio.companyName,
                        portfolio.concept,
                        portfolio.location,
                        portfolio.description,
                        portfolio.status,
                        portfolio.registerStartDate,
                        portfolio.registerEndDate
                )
                .from(portfolio)
                .leftJoin(portfolio.portfolioImages, portfolioImage)
                .on(portfolioImage.imageType.eq(ImageType.THUMBNAIL))
                .where(builder, portfolio.id.in(portfolioId))
                .fetch();

        Map<Long, Tuple> tupleMap = rows.stream()
                .collect(Collectors.toMap(t -> t.get(portfolio.id), Function.identity()));

        List<PfSearchResponse> responses = portfolioId.stream()
                .map(id -> {
                    Tuple r = tupleMap.get(id);
                    return PfSearchResponse.builder()
                            .portfolioId(r.get(portfolio.id))
                            .thumbNailImgUrl(Optional.ofNullable(r.get(portfolioImage.imageUrl)).orElse(""))
                            .companyName(r.get(portfolio.companyName))
                            .concept(r.get(portfolio.concept))
                            .description(r.get(portfolio.description))
                            .location(r.get(portfolio.location))
                            .status(r.get(portfolio.status))
                            .startDate(r.get(portfolio.registerStartDate))
                            .endDate(r.get(portfolio.registerEndDate))
                            .build();
                }).collect(Collectors.toList());


        Long totalCount = jpaQueryFactory
                .select(portfolio.count())
                .from(portfolio)
                .where(builder)
                .fetchOne();

        return SearchResult.<PfSearchResponse> builder()
                .totalCount(totalCount != null ? totalCount : 0)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();
    }

    public SearchResult<PfDisplayResponse> getDisplayPf(CustomUserDetails customUserDetails, PfSearchRequest request, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        QPortfolio portfolio = QPortfolio.portfolio;
        QPortfolioImage portfolioImage = QPortfolioImage.portfolioImage;
        QPortfolioDisplay portfolioDisplay = QPortfolioDisplay.portfolioDisplay;
        BooleanBuilder builder = new BooleanBuilder()
                .and(portfolio.status.ne(PortfolioStatus.DELETED));

        if (StringUtils.hasText(request.getKeyword()) && request.getKey() != null) {
            switch (request.getKey()) {
                case COMPANY_NAME -> builder.and(portfolio.companyName.containsIgnoreCase(request.getKeyword()));
                case CONCEPT -> builder.and(portfolio.concept.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keywordBuilder = new BooleanBuilder();
                    keywordBuilder.or(portfolio.companyName.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(portfolio.concept.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(portfolio.location.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(portfolio.description.containsIgnoreCase(request.getKeyword()));
                    builder.and(keywordBuilder);
                }
            }
        }

        if (request.getRegisterDate() != null) {
            if (request.getRegisterDate().getStartDate() != null) {
                builder.and(portfolio.registerStartDate.goe(request.getRegisterDate().getStartDate()));
            }
            if (request.getRegisterDate().getEndDate() != null) {
                builder.and(portfolio.registerEndDate.loe(request.getRegisterDate().getEndDate()));
            }
        }

        List<Long> portfolioId = jpaQueryFactory
                .select(portfolio.id)
                .from(portfolio)
                .leftJoin(portfolio.portfolioImages, portfolioImage)
                .on(portfolioImage.imageType.eq(ImageType.THUMBNAIL))
                .where(builder)
                .orderBy(portfolioImage.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (portfolioId.isEmpty()) {
            return SearchResult.<PfDisplayResponse>builder()
                    .totalCount(0L)
                    .page(pageable.getPageNumber())
                    .size(pageable.getPageSize())
                    .data(List.of())
                    .build();
        }

        List<Tuple> rows = jpaQueryFactory
                .select(
                        portfolio.id,
                        portfolioImage.imageUrl,
                        portfolio.companyName,
                        portfolio.isPinned,
                        portfolioDisplay.orderIndex,
                        portfolio.createdAt
                )
                .from(portfolio)
                .leftJoin(portfolio.portfolioImages, portfolioImage)
                .on(portfolioImage.imageType.eq(ImageType.THUMBNAIL))
                .leftJoin(portfolio.portfolioDisplays, portfolioDisplay)
                .where(builder)
                .orderBy(
                        portfolio.isPinned.desc().nullsLast(),
                        portfolioDisplay.orderIndex.asc().nullsLast(),
                        portfolio.createdAt.desc())
                .fetch();

        List<PfDisplayResponse> responses = rows.stream()
                .map(r -> PfDisplayResponse.builder()
                        .portfolioId(r.get(portfolio.id))
                        .thumbNailImgUrl(Optional.ofNullable(r.get(portfolioImage.imageUrl)).orElse(""))
                        .companyName(r.get(portfolio.companyName))
                        .orderIndex(r.get(portfolioDisplay.orderIndex))
                        .isPinned(r.get(portfolio.isPinned))
                        .createdAt(r.get(portfolio.createdAt).toLocalDate())
                        .build())
                .collect(Collectors.toList());

        Long totalCount = jpaQueryFactory
                .select(portfolio.count())
                .from(portfolio)
                .where(builder)
                .fetchOne();

        return SearchResult.<PfDisplayResponse> builder()
                .totalCount(totalCount != null ? totalCount : 0)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();

    }

    @Transactional
    public List<PfDisplayUpdateResponse> updateDisplayPf(CustomUserDetails customUserDetails, List<DisplayUpdateRequest> request) {
        globalService.validateAdmin(customUserDetails);

        Set<Integer> indexList = request.stream()
                .map(DisplayUpdateRequest::getOrderIndex)
                .collect(Collectors.toSet());

        List<Long> idList = request.stream()
                .map(DisplayUpdateRequest::getPortfolioId)
                .toList();

        Map<Long, Portfolio> pfMap = portfolioRepository.findAllByIdInAndIsPinnedFalse(idList).stream()
                .collect(Collectors.toMap(Portfolio::getId, Function.identity()));

        if (indexList.size() != request.size()) {
            throw new CustomException(ErrorCode.DUPLICATE_ORDER_VALUE);
        }

        if (pfMap.size() != request.size()) {
            throw new CustomException(ErrorCode.PINNED_CANNOT_HAVE_MANUAL_ORDER);
        }

        portfolioDisplayRepository.deleteAllInBatch();

        List<PortfolioDisplay> toSave = request.stream()
                .sorted(Comparator.comparing(DisplayUpdateRequest::getOrderIndex))
                .map(u -> PortfolioDisplay.builder()
                        .orderIndex(u.getOrderIndex())
                        .updatedBy(customUserDetails.getId())
                        .portfolio(pfMap.get(u.getPortfolioId()))
                        .build())
                .toList();

        List<PortfolioDisplay> displays = portfolioDisplayRepository.saveAll(toSave);

        return displays.stream()
                .sorted(Comparator.comparing(PortfolioDisplay::getOrderIndex))
                .map(d -> PfDisplayUpdateResponse.builder()
                        .portfolioId(d.getPortfolio().getId())
                        .companyName(d.getPortfolio().getCompanyName())
                        .orderIndex(d.getOrderIndex())
                        .isPinned(d.getPortfolio().getIsPinned())
                        .createdAt(d.getCreatedAt().toLocalDate())
                        .build())
                .toList();
    }


    @Transactional
    public PfDisplayUpdateResponse togglePinned(CustomUserDetails customUserDetails, Long portfolioId){
        globalService.validateAdmin(customUserDetails);

        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(() -> new CustomException(ErrorCode.PORTFOLIO_NOT_FOUND));
        Boolean updatePin = portfolio.getIsPinned().equals(true) ? false : true;

        portfolio.updateIsPinned(updatePin, customUserDetails.getId());
        Portfolio saved = portfolioRepository.save(portfolio);

        Integer order = saved.getPortfolioDisplays().stream()
                .findFirst()
                .map(PortfolioDisplay::getOrderIndex)
                .orElse(null);


        return PfDisplayUpdateResponse.builder()
                .portfolioId(saved.getId())
                .companyName(saved.getCompanyName())
                .orderIndex(order)
                .isPinned(portfolio.getIsPinned())
                .createdAt(portfolio.getCreatedAt().toLocalDate())
                .build();
    }
}
