package livart.erp.domain.product.product;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import livart.common.Auth.CustomUserDetails;
import livart.common.domain.product.entity.*;
import livart.common.domain.product.repository.*;
import livart.common.dto.enums.product.*;
import livart.common.dto.request.ProductRegisterRequest;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.mapper.SearchResult;
import livart.common.service.GlobalService;
import livart.erp.domain.product.option.*;
import livart.erp.domain.product.product.dto.request.*;
import livart.erp.domain.product.product.dto.response.*;
import livart.erp.domain.product.productColor.ProductColorResponse;
import livart.erp.domain.product.productGuide.ProductGuideInfoResponse;
import livart.erp.domain.product.productImage.ProductImageResponse;
import livart.erp.domain.product.productColor.ProductColorService;
import livart.erp.domain.product.productGuide.ProductGuideInfoService;
import livart.erp.domain.product.productImage.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static livart.common.domain.product.entity.QProductImage.productImage;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final CategoryRepository categoryRepository;
    private final GlobalService globalService;
    private final ProductColorService productColorService;
    private final ProductRepository productRepository;
    private final OptionService optionService;
    private final ProductImageService productImageService;
    private final ProductGuideInfoService productGuideInfoService;
    private final JPAQueryFactory queryFactory;
    private final CategoryDisplayRepository categoryDisplayRepository;
    private final ProductImageRepository productImageRepository;
    private final OptionRepository optionRepository;
    @Transactional
    public ProductRegisterResponse registerProduct(CustomUserDetails customUserDetails, ProductRegisterRequest request) {
        globalService.validateAdmin(customUserDetails);

        Product product = Product.builder()
                .categoryId(request.getCategoryId())
                .productName(request.getProductName())
                .productCode(request.getProductCode())
                .keyword(request.getKeyword())
                .brand(request.getBrand())
                .status(request.getStatus())
                .restockAlert(request.getRestockAlert())
                .mileageType(request.getMileageType())
                .mileageRate(request.getMileageRate())
                .delPrice(request.getDelPrice())
                .originalPrice(request.getOriginalPrice())
                .salePrice(request.getSalePrice())
                .replaceComment(request.getReplaceComment())
                .optionUsage(request.getOptionUsage())
                .deliveryType(request.getDeliveryType())
                .deliveryText(request.getDeliveryText())
                .deliveryPrice(request.getDeliveryPrice())
                .createdBy(customUserDetails.getId())
                .build();

        Product savedProduct = productRepository.saveAndFlush(product);

        List<ProductColorResponse> productColors =
                productColorService.saveProductColor(customUserDetails, savedProduct, request.getProductColors());

        List<DetailedOptionResponse> detailedOptionResponses = new ArrayList<>();
        List<OptionCombinationResponse> combinationResponses = new ArrayList<>();

        if (Boolean.TRUE.equals(request.getOptionUsage())) {
            OptionListResponse saveOption =
                    optionService.saveOption(customUserDetails, savedProduct, request.getOptionCombinations(), request.getDetailedOptions());

            detailedOptionResponses = saveOption.getDetailedOptionResponse();
            combinationResponses = saveOption.getCombinationResponse();
        }

        List<ProductImageResponse> productImages =
                productImageService.saveProductImage(customUserDetails, savedProduct, request.getProductImageList());

        List<ProductGuideInfoResponse> productGuideInfos =
                productGuideInfoService.saveProductGuideInfo(customUserDetails, savedProduct, request.getProductGuideInfos());

        Product saved = productRepository.save(savedProduct);

        return toProductResponse(saved, productColors, detailedOptionResponses, combinationResponses, productImages, productGuideInfos);

    }

    public ProductRegisterResponse getProduct(CustomUserDetails customUserDetails, Long productId) {
        globalService.validateAdmin(customUserDetails);

        Product product = productRepository.findById(productId).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        List<ProductColorResponse> productColors =
                productColorService.getProductColor(customUserDetails, product);

        OptionListResponse saveOption = optionService.getOption(customUserDetails, product);

        List<DetailedOptionResponse> detailedOptionResponses = saveOption.getDetailedOptionResponse();
        List<OptionCombinationResponse> combinationResponses = saveOption.getCombinationResponse();

        List<ProductImageResponse> productImages =
                productImageService.getProductImage(customUserDetails, product);

        List<ProductGuideInfoResponse> productGuideInfos =
                productGuideInfoService.getProductGuideInfo(customUserDetails, product);

        return toProductResponse(product, productColors, detailedOptionResponses, combinationResponses, productImages, productGuideInfos);
    }

    @Transactional
    public ProductRegisterResponse updateProduct(CustomUserDetails customUserDetails, Long productId, ProductRegisterRequest request) {
        globalService.validateAdmin(customUserDetails);

        Product product = productRepository.findById(productId).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        product.update(request, customUserDetails.getId());

        productColorService.deleteColor(product);

        List<ProductColorResponse> productColors =
                productColorService.updateProductColor(customUserDetails, product, request.getProductColors());

        List<DetailedOptionResponse> detailedOptionResponses = new ArrayList<>();
        List<OptionCombinationResponse> combinationResponses = new ArrayList<>();

        if (Boolean.TRUE.equals(request.getOptionUsage())) {
            OptionListResponse saveOption =
                    optionService.updateOption(customUserDetails, product, request.getOptionCombinations(), request.getDetailedOptions());

            detailedOptionResponses = saveOption.getDetailedOptionResponse();
            combinationResponses = saveOption.getCombinationResponse();
        } else {
            OptionListResponse saveOption = optionService.getOption(customUserDetails, product);

            detailedOptionResponses = saveOption.getDetailedOptionResponse();
            combinationResponses = saveOption.getCombinationResponse();
        }

        productImageService.deleteImage(product);

        List<ProductImageResponse> productImages =
                productImageService.updateProductImage(customUserDetails, product, request.getProductImageList());

        productGuideInfoService.deleteGuide(product);

        List<ProductGuideInfoResponse> productGuideInfos =
                productGuideInfoService.updateProductGuideInfo(customUserDetails, product, request.getProductGuideInfos());

        return toProductResponse(product, productColors, detailedOptionResponses, combinationResponses, productImages, productGuideInfos);
    }

    private ProductRegisterResponse toProductResponse(Product product,
                                                      List<ProductColorResponse> productColors,
                                                      List<DetailedOptionResponse> detailedOptionResponses,
                                                      List<OptionCombinationResponse> combinationResponses,
                                                      List<ProductImageResponse> productImages,
                                                      List<ProductGuideInfoResponse> productGuideInfos) {

        return ProductRegisterResponse.builder()
                .productId(product.getId())
                .categoryId(product.getCategoryId())
                .productName(product.getProductName())
                .productCode(product.getProductCode())
                .keyword(product.getKeyword())
                .productColors(productColors)
                .brand(product.getBrand())
                .status(product.getStatus())
                .restockAlert(product.getRestockAlert())
                .mileageType(product.getMileageType())
                .mileageRate(product.getMileageRate())
                .delPrice(product.getDelPrice())
                .originalPrice(product.getOriginalPrice())
                .salePrice(product.getSalePrice())
                .replaceComment(product.getReplaceComment())
                .optionUsage(product.getOptionUsage())
                .detailedOptions(detailedOptionResponses)
                .optionCombinations(combinationResponses)
                .productImages(productImages)
                .deliveryPrice(product.getDeliveryPrice())
                .deliveryText(product.getDeliveryText())
                .deliveryType(product.getDeliveryType())
                .productGuideInfos(productGuideInfos)
                .createdAt(product.getCreatedAt())
                .build();
    }

    public SearchResult<ProductSearchResponse> getProductSearchList(CustomUserDetails customUserDetails, ProductSearchRequest request, Pageable pageable) {
        globalService.validateAdmin(customUserDetails);
        List<Long> idList = findAllDescendantCategoryIds(request.getCategoryId());

        QProduct product = QProduct.product;
        QOption option = QOption.option;
        BooleanBuilder builder = new BooleanBuilder();

        if (request.getProductStatus() == null || request.getProductStatus() == ProductStatus.ACTIVE) {
            builder.and(option.productStatus.eq(ProductStatus.ACTIVE));
        } else {
            builder.and(option.productStatus.eq(request.getProductStatus()));
        }

        if (StringUtils.hasText(request.getKeyword()) && request.getKey() != null) {
            switch (request.getKey()) {
                case PRODUCT_CODE -> builder.and(product.productCode.containsIgnoreCase(request.getKeyword()));
                case PRODUCT_NAME -> builder.and(product.productName.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keywordBuilder = new BooleanBuilder();
                    keywordBuilder.or(product.productName.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(product.productCode.containsIgnoreCase(request.getKeyword()));
                    builder.and(keywordBuilder);
                }
            }
        }

        if (request.getRegisterDate() != null) {
            if (request.getRegisterDate().getStartDate() != null) {
                builder.and(option.createdAt.goe(request.getRegisterDate().getStartDate().atStartOfDay()));
            }
            if (request.getRegisterDate().getEndDate() != null) {
                builder.and(option.createdAt.loe(request.getRegisterDate().getEndDate().atTime(23, 59, 59)));
            }
        }

        if (request.getCategoryId() != null) {
            builder.and(product.categoryId.in(idList));
        }

        if (request.getBrandType() != null) {
            builder.and(product.brand.eq(request.getBrandType()));
        }

        if (request.getMinPrice() != null) {
            builder.and(product.salePrice.goe(request.getMinPrice()));
        }

        if (request.getMaxPrice() != null) {
            builder.and(product.salePrice.loe(request.getMaxPrice()));
        }

        List<Option> options = queryFactory
                .selectFrom(option)
                .join(option.product, product).fetchJoin()
                .where(builder)
                .orderBy(option.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<ProductSearchResponse> responses = options.stream()
                .map(o -> {
                    BigDecimal salePrice = o.getProduct().getSalePrice().add(o.getPrice());
                    BigDecimal originalPrice = o.getProduct().getOriginalPrice().add(o.getPrice());
                    BigDecimal discountRate = calculateDiscountRate(salePrice, originalPrice);
                    String discount = discountRate.toPlainString() + "%";

                    return ProductSearchResponse.builder()
                            .productId(o.getProduct().getId())
                            .optionId(o.getId())
                            .optionCode(o.getOptionCode())
                            .imageUrl(o.getImageUrl())
                            .productName(o.getProduct().getProductName() +"/"+ o.getOptionName())
                            .status(o.getStatus())
                            .salePrice(salePrice)
                            .originalPrice(originalPrice)
                            .registerAt(o.getCreatedAt())
                            .updatedAt(o.getUpdatedAt())
                            .discountRate(discount)
                            .build();
                }).collect(Collectors.toList());

        Long totalCount = queryFactory
                .select(option.count())
                .from(option)
                .where(builder)
                .fetchOne();

        return SearchResult.<ProductSearchResponse>builder()
                .totalCount(totalCount)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();
    }

    public SearchResult<ProductAddSearchResponse> addProductSearch(CustomUserDetails customUserDetails, ProductAddRequest request, Pageable pageable) {
        globalService.validateAdmin(customUserDetails);

        QProduct product = QProduct.product;
        QProductImage productImage = QProductImage.productImage;
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(product.productStatus.eq(ProductStatus.ACTIVE));

        if (StringUtils.hasText(request.getKeyword()) && request.getKey() != null) {
            switch (request.getKey()) {
                case PRODUCT_CODE -> builder.and(product.productCode.containsIgnoreCase(request.getKeyword()));
                case PRODUCT_NAME -> builder.and(product.productName.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keywordBuilder = new BooleanBuilder();
                    keywordBuilder.or(product.productName.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(product.productCode.containsIgnoreCase(request.getKeyword()));
                    builder.and(keywordBuilder);
                }
            }
        }
        if (request.getLastCategoryId() != null) {
            List<Long> categoryIds = findAllDescendantCategoryIds(request.getLastCategoryId());
            builder.and(product.categoryId.in(categoryIds));
        }

        if (request.getBrandType() != null) {
            builder.and(product.brand.eq(request.getBrandType()));
        }

        List<Tuple> query = queryFactory
                .select(
                        product.id,
                        product.categoryId,
                        product.productName,
                        product.productCode,
                        productImage.imageUrl
                )
                .from(product)
                .where(builder)
                .leftJoin(productImage).on(productImage.product.eq(product).and(productImage.imageType.eq(ImageType.THUMBNAIL)))
                .orderBy(product.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long countResult = queryFactory
                .select(product.count())
                .from(product)
                .where(builder)
                .fetchOne();

        long totalCount = (countResult != null) ? countResult : 0L;

        List<ProductAddSearchResponse> responses = query.stream()
                .map(q -> ProductAddSearchResponse.builder()
                        .productId(q.get(product.id))
                        .categoryId(q.get(product.categoryId))
                        .productName(q.get(product.productName))
                        .productCode(q.get(product.productCode))
                        .thumbNailImgUrl(q.get(productImage.imageUrl))
                        .build()
                ).collect(Collectors.toList());

        return SearchResult.<ProductAddSearchResponse>builder()
                .totalCount(totalCount)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();
    }

    public ProductAddDto getProductDetail(CustomUserDetails customUserDetails, Long productId) {
        globalService.validateAdmin(customUserDetails);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        ProductImage thumbnails = productImageRepository.findByProductAndImageType(product, ImageType.THUMBNAIL);

        List<OptionAddResponse> optionResponse = new ArrayList<>();

        if (product.getOptionUsage()) {
            optionResponse = optionService.getOptionsForProduct(product);
        }

        return ProductAddDto.builder()
                .productId(product.getId())
                .productName(product.getProductName())
                .productCode(product.getProductCode())
                .thumbNailImgUrl(thumbnails.getImageUrl()) // 없을 수도 있으니 null safe
                .brand(product.getBrand())
                .status(product.getStatus())
                .quantity(1)
                .unitDelPrice(product.getSalePrice())
                .supplyPrice(product.getSalePrice())
                .replaceComment(product.getReplaceComment())
                .options(optionResponse)
                .build();
    }

    @Transactional
    public List<ProductStockResponse> updateOutStock(CustomUserDetails customUserDetails, IdListRequest request) {
        globalService.validateAdmin(customUserDetails);

        List<Option> productList = optionRepository.findAllById(request.getProductIdList());

        productList.forEach(product -> product.updateStatus(StockStatus.OUT_STOCK, customUserDetails.getId()));

        return productList.stream()
                .map(product -> ProductStockResponse.builder()
                        .optionId(product.getId())
                        .optionCode(product.getOptionCode())
                        .optionName(product.getProduct().getProductName() +"/"+ product.getOptionName())
                        .status(product.getStatus())
                        .build()
                ).collect(Collectors.toList());
    }

    @Transactional
    public List<ProductStockResponse> updateInStock(CustomUserDetails customUserDetails, IdListRequest request) {
        globalService.validateAdmin(customUserDetails);

        List<Option> productList = optionRepository.findAllById(request.getProductIdList());

        productList.forEach(product -> product.updateStatus(StockStatus.IN_STOCK, customUserDetails.getId()));

        return productList.stream()
                .map(product -> ProductStockResponse.builder()
                        .optionId(product.getId())
                        .optionCode(product.getOptionCode())
                        .optionName(product.getProduct().getProductName() +"/"+ product.getOptionName())
                        .status(product.getStatus())
                        .build()
                ).collect(Collectors.toList());
    }

    @Transactional
    public List<ProductDeactiveResponse> updateDeactivate(CustomUserDetails customUserDetails, IdListRequest request) {
        globalService.validateAdmin(customUserDetails);

        List<Option> productList = optionRepository.findAllById(request.getProductIdList());

        productList.forEach(product -> product.deactivate(ProductStatus.DELETE, customUserDetails.getId()));

        return productList.stream()
                .map(product -> ProductDeactiveResponse.builder()
                        .optionId(product.getId())
                        .optionCode(product.getOptionCode())
                        .optionName(product.getProduct().getProductName() +"/"+ product.getOptionName())
                        .status(product.getProductStatus())
                        .build()
                ).collect(Collectors.toList());
    }

    @Transactional
    public List<ProductDeactiveResponse> updateRestore(CustomUserDetails customUserDetails, IdListRequest request) {
        globalService.validateAdmin(customUserDetails);

        List<Option> productList = optionRepository.findAllById(request.getProductIdList());

        productList.forEach(product -> product.deactivate(ProductStatus.ACTIVE, customUserDetails.getId()));

        return productList.stream()
                .map(product -> ProductDeactiveResponse.builder()
                        .optionId(product.getId())
                        .optionCode(product.getOptionCode())
                        .optionName(product.getProduct().getProductName() +"/"+ product.getOptionName())
                        .status(product.getProductStatus())
                        .build()
                ).collect(Collectors.toList());
    }

    public SearchResult<ProductSummaryResponse> getProductStockList(CustomUserDetails customUserDetails, ProductBatchRequest request, Pageable pageable) {
        globalService.validateAdmin(customUserDetails);

        QProduct product = QProduct.product;
        QOption option = QOption.option;
        BooleanBuilder builder = new BooleanBuilder()
                .and(option.status.eq(StockStatus.OUT_STOCK));

        if (request.getProductStatus() == null || request.getProductStatus() == ProductStatus.ACTIVE) {
            builder.and(option.productStatus.eq(ProductStatus.ACTIVE));
        } else {
            builder.and(option.productStatus.eq(request.getProductStatus()));
        }

        if (StringUtils.hasText(request.getKeyword()) && request.getKey() != null) {
            switch (request.getKey()) {
                case PRODUCT_CODE -> builder.and(product.productCode.containsIgnoreCase(request.getKeyword()));
                case PRODUCT_NAME -> builder.and(product.productName.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keywordBuilder = new BooleanBuilder();
                    keywordBuilder.or(product.productName.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(product.productCode.containsIgnoreCase(request.getKeyword()));
                    builder.and(keywordBuilder);
                }
            }
        }

        if (request.getRegisterDate() != null) {
            if (request.getRegisterDate().getStartDate() != null) {
                builder.and(product.createdAt.goe(request.getRegisterDate().getStartDate().atStartOfDay()));
            }
            if (request.getRegisterDate().getEndDate() != null) {
                builder.and(product.createdAt.loe(request.getRegisterDate().getEndDate().atTime(23, 59, 59)));
            }
        }

        List<Option> options = queryFactory
                .selectFrom(option)
                .join(option.product, product).fetchJoin()
                .where(builder)
                .orderBy(option.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<ProductSummaryResponse> responses = options.stream()
                .map(o -> ProductSummaryResponse.builder()
                            .productId(o.getProduct().getId())
                            .optionId(o.getId())
                            .optionCode(o.getOptionCode())
                            .imageUrl(o.getImageUrl())
                            .productName(o.getProduct().getProductName())
                            .optionName(o.getOptionName())
                            .status(o.getStatus())
                            .build()
                ).collect(Collectors.toList());

        Long totalCount = queryFactory
                .select(option.count())
                .from(option)
                .where(builder)
                .fetchOne();

        return SearchResult.<ProductSummaryResponse>builder()
                .totalCount(totalCount)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();

    }

    /*
    @Transactional
    public List<ProductDeactiveResponse> updatePrice(CustomUserDetails customUserDetails, PriceBatchChangeRequest request) {
        globalService.validateAdmin(customUserDetails);

        List<Product> productList = productRepository.findAllById(request.getProductIdList());

        productList.forEach(product -> product.changePrice(request.getChangePrice(), customUserDetails.getId()));

        return productList.stream()
                .map(product -> ProductDeactiveResponse.builder()
                        .optionId(product.getId())
                        .optionCode(product.getOptionCode())
                        .optionName(product.getProduct().getProductName() + product.getOptionName())
                        .status(product.getProductStatus())
                        .build()
                ).collect(Collectors.toList());
    }
    */

    public SearchResult<ProductDisplayResponse> getDisplay(CustomUserDetails customUserDetails, Long categoryId, Pageable pageable) {
        globalService.validateAdmin(customUserDetails);

        QProduct p = QProduct.product;
        QCategoryDisplay o = QCategoryDisplay.categoryDisplay;

        // 1. 하위 카테고리 포함 ID 조회
        List<Long> categoryIds = findAllDescendantCategoryIds(categoryId);

        JPQLQuery<ProductDisplayResponse> query = queryFactory
                .select(Projections.constructor(ProductDisplayResponse.class,
                        p.id,
                        o.manualOrder,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(productImage.imageUrl)
                                        .from(productImage)
                                        .where(productImage.product.id.eq(p.id),
                                                productImage.imageType.eq(ImageType.THUMBNAIL))
                                        .limit(1),
                                "imageUrl"
                        ),
                        p.productName,
                        p.salePrice,
                        p.isPinned,
                        p.createdAt
                ))
                .from(p)
                .leftJoin(o).on(
                        p.id.eq(o.product.id),
                        o.category.id.eq(categoryId)
                )
                .where(
                        p.productStatus.eq(ProductStatus.ACTIVE),
                        p.categoryId.in(categoryIds)
                )
                .orderBy(
                        p.isPinned.desc().nullsLast(),
                        o.manualOrder.asc().nullsLast(),
                        p.createdAt.desc()
                );

        long totalCount = queryFactory
                .select(p.count())
                .from(p)
                .where(p.productStatus.eq(ProductStatus.ACTIVE), p.categoryId.in(categoryIds))
                .fetchOne();

        List<ProductDisplayResponse> responses = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return SearchResult.<ProductDisplayResponse>builder()
                .totalCount(totalCount)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();

    }
    public List<Long> findAllDescendantCategoryIds(Long parentId) {
        List<Long> result = new ArrayList<>();
        collectChildren(parentId, result);
        return result;
    }

    private void collectChildren(Long parentId, List<Long> result) {
        result.add(parentId);
        Category parent = categoryRepository.findById(parentId).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        List<Category> children = categoryRepository.findByParent(parent);
        for (Category child : children) {
            collectChildren(child.getId(), result);
        }
    }

    @Transactional
    public List<ProductDisplayResponse> updateDisplay(CustomUserDetails customUserDetails, Long categoryId, List<ProductOrderUpdateRequest> requests) {
        globalService.validateAdmin(customUserDetails);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        // 1. 중복 manualOrder 체크 (null 제외)
        List<Integer> manualOrders = requests.stream()
                .map(ProductOrderUpdateRequest::getManualOrder)
                .filter(Objects::nonNull)
                .toList();

        if (manualOrders.size() != new HashSet<>(manualOrders).size()) {
            throw new CustomException(ErrorCode.DUPLICATE_ORDER_VALUE);
        }

        categoryDisplayRepository.deleteAllByCategoryId(category.getId());

        Map<Long, ProductOrderUpdateRequest> requestMap = requests.stream()
                .collect(Collectors.toMap(ProductOrderUpdateRequest::getProductId, Function.identity()));

        // 4. 실제 Product 조회
        List<Product> products = productRepository.findAllById(requestMap.keySet());

        // 5. product → request 매핑 후 manualOrder 기준 정렬
        List<Product> sortedProducts = products.stream()
                .sorted(Comparator.comparing(
                        p -> Optional.ofNullable(requestMap.get(p.getId()).getManualOrder()).orElse(Integer.MAX_VALUE)
                ))
                .toList();

        // 6. 연속된 manualOrder 재지정
        List<CategoryDisplay> toSave = new ArrayList<>();
        int manualOrder = 1;

        for (Product product : sortedProducts) {
            if (Boolean.TRUE.equals(product.getIsPinned())) {
                throw new CustomException(ErrorCode.PINNED_PRODUCT_CANNOT_HAVE_MANUAL_ORDER);
            }

            CategoryDisplay display = CategoryDisplay.builder()
                    .category(category)
                    .product(product)
                    .manualOrder(manualOrder++) // 연속값 부여
                    .createdBy(customUserDetails.getId())
                    .build();

            toSave.add(display);
        }

        List<CategoryDisplay> displays = categoryDisplayRepository.saveAll(toSave);

        // 7. 응답 반환: manualOrder 기준 정렬
        return displays.stream()
                .sorted(Comparator.comparing(CategoryDisplay::getManualOrder, Comparator.nullsLast(Integer::compareTo)))
                .map(d -> ProductDisplayResponse.builder()
                        .productId(d.getProduct().getId())
                        .manualOrder(d.getManualOrder())
                        .imageUrl(getThumbNail(d.getProduct()).getImageUrl())
                        .productName(d.getProduct().getProductName())
                        .salePrice(d.getProduct().getSalePrice())
                        .isPinned(d.getProduct().getIsPinned())
                        .createdAt(d.getCreatedAt())
                        .build())
                .toList();
    }



    @Transactional
    public ProductDisplayResponse togglePinned(CustomUserDetails customUserDetails, Long productId) {
        globalService.validateAdmin(customUserDetails);

        Product product = productRepository.findById(productId).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        product.setIsPinned(!Boolean.TRUE.equals(product.getIsPinned()));

        if (product.getIsPinned()) {
            List<CategoryDisplay> displays = categoryDisplayRepository.findByProduct(product);
            categoryDisplayRepository.deleteAll(displays);
        }

        return ProductDisplayResponse.builder()
                .productId(product.getId())
                .imageUrl(getThumbNail(product).getImageUrl())
                .productName(product.getProductName())
                .salePrice(product.getSalePrice())
                .isPinned(product.getIsPinned())
                .createdAt(product.getCreatedAt())
                .build();
    }

    private ProductImage getThumbNail(Product product) {
        return product.getProductImages().stream()
                .filter(p -> p.getImageType().equals(ImageType.THUMBNAIL))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    public static BigDecimal calculateDiscountRate(BigDecimal a, BigDecimal b) {

        BigDecimal max = a.max(b);
        BigDecimal min = a.min(b);

        BigDecimal discountRate = BigDecimal.ONE
                .subtract(min.divide(max, 10, RoundingMode.HALF_UP))
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        return discountRate;
    }

}