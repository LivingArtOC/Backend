package livart.erp.domain.promotion;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import livart.common.Auth.CustomUserDetails;
import livart.common.domain.promotion.entity.*;
import livart.common.domain.promotion.repository.*;
import livart.common.dto.enums.coupon.*;
import livart.common.dto.enums.design.CatalogType;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.mapper.SearchResult;
import livart.common.service.GlobalService;
import livart.erp.domain.promotion.dto.request.*;
import livart.erp.domain.promotion.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final GlobalService globalService;
    private final CatalogRepository catalogRepository;
    private final CouponSettingRepository couponSettingRepository;
    private final CouponRegisterMapper couponRegisterMapper;
    private final CouponRepository couponRepository;
    private final CouponAutoGrantRepository couponAutoGrantRepository;
    private final CouponAutoSettingRepository couponAutoSettingRepository;
    private final JPAQueryFactory queryFactory;
    
    @Transactional
    public List<CatalogResponse> saveCatalog(CustomUserDetails customUserDetails, CatalogRequest request){
        globalService.validateAdmin(customUserDetails);

        Catalog haum = catalogRepository.findCatalogByCatalogType(CatalogType.HAUM)
                .map(existing -> {
                    existing.update(request.getHaumFileName(), request.getHaumFileUrl(), customUserDetails.getId());
                    return existing;
                })
                .orElseGet(() -> Catalog.builder()
                        .catalogType(CatalogType.HAUM)
                        .fileName(request.getHaumFileName())
                        .fileUrl(request.getHaumFileUrl())
                        .createdBy(customUserDetails.getId())
                        .build());

        Catalog office = catalogRepository.findCatalogByCatalogType(CatalogType.OFFICE)
                .map(existing -> {
                    existing.update(request.getOfficeFileName(), request.getOfficeFileUrl(), customUserDetails.getId());
                    return existing;
                })
                .orElseGet(() -> Catalog.builder()
                        .catalogType(CatalogType.OFFICE)
                        .fileName(request.getOfficeFileName())
                        .fileUrl(request.getOfficeFileUrl())
                        .createdBy(customUserDetails.getId())
                        .build());
        
        Catalog savedHaum = catalogRepository.save(haum);
        Catalog savedOffice = catalogRepository.save(office);
        
        CatalogResponse haumResponse = CatalogResponse.builder()
                .catalogId(savedHaum.getId())
                .catalogType(savedHaum.getCatalogType())
                .fileName(savedHaum.getFileName())
                .fileUrl(savedHaum.getFileUrl())
                .build();

        CatalogResponse officeResponse = CatalogResponse.builder()
                .catalogId(savedOffice.getId())
                .catalogType(savedOffice.getCatalogType())
                .fileName(savedOffice.getFileName())
                .fileUrl(savedOffice.getFileUrl())
                .build();

        return List.of(haumResponse, officeResponse);
    }

    public List<CatalogResponse> getCatalog(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        Catalog haum = catalogRepository.findCatalogByCatalogType(CatalogType.HAUM).orElse(null);
        Catalog office = catalogRepository.findCatalogByCatalogType(CatalogType.OFFICE).orElse(null);

        List<CatalogResponse> responses = new ArrayList<>();

        if(haum != null){
            responses.add(CatalogResponse.builder()
                    .catalogId(haum.getId())
                    .catalogType(haum.getCatalogType())
                    .fileName(haum.getFileName())
                    .fileUrl(haum.getFileUrl())
                    .build());
        }

        if(office != null){
            responses.add(CatalogResponse.builder()
                    .catalogId(office.getId())
                    .catalogType(office.getCatalogType())
                    .fileName(office.getFileName())
                    .fileUrl(office.getFileUrl())
                    .build());
        }

        return responses;
    }

    @Transactional
    public CouponSettingResponse saveCouponSetting(CustomUserDetails customUserDetails, CouponSettingRequest request){
        globalService.validateAdmin(customUserDetails);

        CouponSetting couponSetting = couponSettingRepository.findById(1L)
                .map(existing -> {
                    existing.update(request.getType(), request.getPurchaseStandard(), request.getRestoreCoupon(), customUserDetails.getId());
                    return existing;
                })
                .orElseGet(() -> CouponSetting.builder()
                        .type(request.getType())
                        .standard(request.getPurchaseStandard())
                        .restoreCoupon(request.getRestoreCoupon())
                        .createdBy(customUserDetails.getId())
                        .build());

        CouponSetting saved = couponSettingRepository.save(couponSetting);

        return CouponSettingResponse.builder()
                .settingId(saved.getId())
                .type(saved.getType())
                .purchaseStandard(saved.getStandard())
                .restoreCoupon(saved.getRestoreCoupon())
                .build();
    }

    public CouponSettingResponse getCouponSetting(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        CouponSetting couponSetting = couponSettingRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.COUPON_SETTING_NOT_FOUND));

        return CouponSettingResponse.builder()
                .settingId(couponSetting.getId())
                .type(couponSetting.getType())
                .purchaseStandard(couponSetting.getStandard())
                .restoreCoupon(couponSetting.getRestoreCoupon())
                .build();
    }

    @Transactional
    public CouponRegisterResponse saveCoupon(CustomUserDetails customUserDetails, CouponRegisterRequest request){
        globalService.validateAdmin(customUserDetails);

        Coupon coupon = couponRegisterMapper.toEntity(request).toBuilder()
                .createdBy(customUserDetails.getId())
                .build();

        Coupon saved = couponRepository.save(coupon);

        return couponRegisterMapper.toDto(saved).toBuilder()
                .couponId(saved.getId())
                .build();
    }

    public CouponRegisterResponse getCoupon(CustomUserDetails customUserDetails, Long couponId){
        globalService.validateAdmin(customUserDetails);

        Coupon coupon = couponRepository.findById(couponId).orElseThrow(() -> new CustomException(ErrorCode.COUPON_NOT_FOUND));

        return couponRegisterMapper.toDto(coupon).toBuilder()
                .couponId(coupon.getId())
                .build();
    }

    @Transactional
    public CouponRegisterResponse updateCoupon(CustomUserDetails customUserDetails, CouponRegisterRequest request, Long couponId) {
        globalService.validateAdmin(customUserDetails);

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CustomException(ErrorCode.COUPON_NOT_FOUND));

        coupon.update(request, customUserDetails.getId());
        Coupon saved = couponRepository.save(coupon);

        return couponRegisterMapper.toDto(saved).toBuilder()
                .couponId(saved.getId())
                .build();
    }
    public List<CouponAutoResponse> getCouponAutoInfo(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        List<CouponAutoSetting> couponAutoSettings = couponAutoSettingRepository.findAllWithGraph();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<CouponAutoResponse> couponAutoGrantsList = couponAutoSettings.stream()
                .map(c -> {
                            TriggerEvents triggerEvents = c.getTriggerEvents();
                            Boolean enabled = c.getEnabled();

                            List<AutoCouponList> couponList = c.getCouponAutoGrants().stream()
                                    .map(g -> {
                                        String expireDate = (g.getCoupon().getCouponExpiration() == CouponExpiration.FIX_DATE && g.getCoupon().getExpireEndDate() != null)
                                                ? g.getCoupon().getExpireEndDate().format(formatter)
                                                : "발급일 기준 " + g.getCoupon().getIssuedDate() + "일";

                                        String discountRate = (g.getCoupon().getCouponDiscountType() == CouponDiscountType.FIXED)
                                                ? String.format("%,d원", g.getCoupon().getDiscountPrice())
                                                : String.format("%d%%", g.getCoupon().getDiscountPrice());

                                        return AutoCouponList.builder()
                                                .couponId(g.getCoupon().getId())
                                                .couponName(g.getCoupon().getCouponName())
                                                .code(g.getCoupon().getCode())
                                                .expireDate(expireDate)
                                                .discountRate(discountRate)
                                                .build();
                                    }).collect(Collectors.toList());

                            return CouponAutoResponse.builder()
                                    .triggerEvents(triggerEvents)
                                    .enabled(enabled)
                                    .couponLists(couponList)
                                    .build();
                        }
                ).collect(Collectors.toList());
        return couponAutoGrantsList;
    }

    @Transactional
    public List<CouponAutoResponse> saveCouponAutoInfo(CustomUserDetails customUserDetails, List<CouponAutoRequest> request) {
        globalService.validateAdmin(customUserDetails);

        couponAutoGrantRepository.deleteAll();
        couponAutoSettingRepository.deleteAll();

        Set<Long> idList = request.stream()
                .map(CouponAutoRequest::getCouponIdList)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        Map<Long, Coupon> coupons = couponRepository.findAllByIdIn(idList).stream()
                        .collect(Collectors.toMap(Coupon::getId, Function.identity()));

        List<CouponAutoSetting> settings = new ArrayList<>();

        for(CouponAutoRequest req : request){
            CouponAutoSetting couponAutoSetting = CouponAutoSetting.builder()
                    .triggerEvents(req.getTriggerEvents())
                    .enabled(req.getEnabled())
                    .updatedBy(customUserDetails.getId())
                    .build();

            List<CouponAutoGrant> couponAutoGrant = req.getCouponIdList().stream()
                    .map(id -> CouponAutoGrant.builder()
                                .updatedBy(customUserDetails.getId())
                                .couponAutoSetting(couponAutoSetting)
                                .coupon(coupons.get(id))
                                .build()
                    ).collect(Collectors.toList());

            couponAutoSetting.getCouponAutoGrants().addAll(couponAutoGrant);
            settings.add(couponAutoSetting);
        }

        couponAutoSettingRepository.saveAll(settings);

        List<CouponAutoSetting> couponAutoSettings = couponAutoSettingRepository.findAllWithGraph();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<CouponAutoResponse> couponAutoGrantsList = couponAutoSettings.stream()
                .map(c -> {
                            TriggerEvents triggerEvents = c.getTriggerEvents();
                            Boolean enabled = c.getEnabled();

                            List<AutoCouponList> couponList = c.getCouponAutoGrants().stream()
                                    .map(g -> {
                                        String expireDate = (g.getCoupon().getCouponExpiration() == CouponExpiration.FIX_DATE && g.getCoupon().getExpireEndDate() != null)
                                                ? g.getCoupon().getExpireEndDate().format(formatter)
                                                : "발급일 기준 " + g.getCoupon().getIssuedDate() + "일";

                                        String discountRate = (g.getCoupon().getCouponDiscountType() == CouponDiscountType.FIXED)
                                                ? String.format("%,d원", g.getCoupon().getDiscountPrice())
                                                : String.format("%d%%", g.getCoupon().getDiscountPrice());

                                        return AutoCouponList.builder()
                                                .couponId(g.getCoupon().getId())
                                                .couponName(g.getCoupon().getCouponName())
                                                .code(g.getCoupon().getCode())
                                                .expireDate(expireDate)
                                                .discountRate(discountRate)
                                                .build();
                                    }).collect(Collectors.toList());

                            return CouponAutoResponse.builder()
                                    .triggerEvents(triggerEvents)
                                    .enabled(enabled)
                                    .couponLists(couponList)
                                    .build();
                        }
                ).collect(Collectors.toList());
        return couponAutoGrantsList;
    }

    public List<AutoCouponList> getIssuedCouponList(CustomUserDetails customUserDetails, IssuedCouponResearchRequest request){
        globalService.validateAdmin(customUserDetails);

        QCoupon coupon = QCoupon.coupon;
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(request.getKeyword()) && request.getKey() != null) {
            switch (request.getKey()) {
                case COUPON_NAME -> builder.and(coupon.couponName.containsIgnoreCase(request.getKeyword()));
                case CODE -> builder.and(coupon.code.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keywordBuilder = new BooleanBuilder();
                    keywordBuilder.or(coupon.couponName.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(coupon.code.containsIgnoreCase(request.getKeyword()));
                    builder.and(keywordBuilder);
                }
            }
        }

        List<Coupon> couponList = queryFactory
                .selectFrom(coupon)
                .where(builder)
                .orderBy(coupon.createdAt.desc())
                .fetch();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<AutoCouponList> couponLists = couponList.stream()
                .map(c -> {
                    String expireDate = (c.getCouponExpiration() == CouponExpiration.FIX_DATE && c.getExpireEndDate() != null)
                            ? c.getExpireEndDate().format(formatter)
                            : "발급일 기준 " + c.getIssuedDate() + "일";

                    String discountRate = (c.getCouponDiscountType() == CouponDiscountType.FIXED)
                            ? String.format("%,d원", c.getDiscountPrice())
                            : String.format("%d%%", c.getDiscountPrice());

                    return AutoCouponList.builder()
                            .couponId(c.getId())
                            .code(c.getCode())
                            .couponName(c.getCouponName())
                            .expireDate(expireDate)
                            .discountRate(discountRate)
                            .build();

                        }).collect(Collectors.toList());

        return couponLists;
    }

    public SearchResult<CouponSearchResponse> getCouponList(CustomUserDetails customUserDetails,
                                                            CouponSearchRequest request,
                                                            Pageable pageable) {
        globalService.validateAdmin(customUserDetails);

        QCoupon coupon = QCoupon.coupon;
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(request.getKeyword()) && request.getKey() != null) {
            switch (request.getKey()) {
                case COUPON_NAME -> builder.and(coupon.couponName.containsIgnoreCase(request.getKeyword()));
                case CODE -> builder.and(coupon.code.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keywordBuilder = new BooleanBuilder();
                    keywordBuilder.or(coupon.couponName.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(coupon.code.containsIgnoreCase(request.getKeyword()));
                    builder.and(keywordBuilder);
                }
            }
        }

        if (request.getCouponRegister() != null) {
            if (request.getCouponRegister().getStartDate() != null) {
                builder.and(coupon.createdAt.goe(request.getCouponRegister().getStartDate().atStartOfDay()));
            }
            if (request.getCouponRegister().getEndDate() != null) {
                builder.and(coupon.createdAt.loe(request.getCouponRegister().getEndDate().atTime(23, 59, 59)));
            }
        }

        if (request.getCouponExpire() != null) {
            if (request.getCouponExpire().getStartDate() != null) {
                builder.and(coupon.expireEndDate.goe(request.getCouponExpire().getStartDate()));
            }
            if (request.getCouponExpire().getEndDate() != null) {
                builder.and(coupon.expireEndDate.loe(request.getCouponExpire().getEndDate()));
            }
        }

        if (request.getCouponType() != null && request.getCouponType() != CouponType.ALL) {
            builder.and(coupon.couponType.eq(request.getCouponType()));
        }
        if (request.getIssuedMethod() != null && request.getIssuedMethod() != IssuedMethod.ALL) {
            builder.and(coupon.issuedMethod.eq(request.getIssuedMethod()));
        }
        if (request.getIssuedStatus() != null && request.getIssuedStatus() != IssuedStatus.ALL) {
            builder.and(coupon.issuedStatus.eq(request.getIssuedStatus()));
        }
        if (request.getCouponDiscountType() != null && request.getCouponDiscountType() != CouponDiscountType.ALL) {
            builder.and(coupon.couponDiscountType.eq(request.getCouponDiscountType()));
        }

        List<Coupon> couponList = queryFactory
                .selectFrom(coupon)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(coupon.createdAt.desc())
                .fetch();

        Long totalCount = Optional.ofNullable(
                queryFactory.select(coupon.count())
                        .from(coupon)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        List<CouponSearchResponse> data = couponList.stream()
                .map(c -> CouponSearchResponse.builder()
                        .couponId(c.getId())
                        .couponName(c.getCouponName())
                        .registeredAt(c.getCreatedAt().toLocalDate())
                        .couponType(c.getCouponType())
                        .expireDate(formatExpireDate(c))
                        .discountRate(getDiscountRateText(c))
                        .issuedMethod(c.getIssuedMethod())
                        .issuedStatus(c.getIssuedStatus())
                        .description(c.getDescription())
                        .build())
                .toList();

        return SearchResult.<CouponSearchResponse>builder()
                .totalCount(totalCount)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(data)
                .build();
    }

    private String formatExpireDate(Coupon coupon) {
        String expireDate = (coupon.getCouponExpiration() == CouponExpiration.FIX_DATE)
                ? coupon.getExpireEndDate().toString()
                : "발급일 기준" + coupon.getIssuedDate() + "일";
        
        return expireDate;
    }

    private String getDiscountRateText(Coupon coupon) {
        String discountRate = (coupon.getCouponDiscountType() == CouponDiscountType.FIXED)
                ? coupon.getDiscountPrice() + "원"
                : coupon.getDiscountPrice() + "%";
        return discountRate;
    }

    @Transactional
    public List<CouponSearchResponse> updateCouponStatus(CustomUserDetails customUserDetails, List<Long> couponIdList, IssuedStatus issuedStatus){
        globalService.validateAdmin(customUserDetails);
        
        List<Coupon> couponList = couponRepository.findAllById(couponIdList);

        if (couponList.size() != couponIdList.size()) {
            throw new CustomException(ErrorCode.COUPON_NOT_FOUND); // 일부 누락되었을 경우
        }

        IssuedStatus status;

        switch (issuedStatus) {
            case ACTIVE -> status = IssuedStatus.ACTIVE;
            case PAUSE -> status = IssuedStatus.PAUSE;
            case RESTRICT -> status = IssuedStatus.RESTRICT;
            default -> throw new CustomException(ErrorCode.INVALID_COUPON_ISSUED_STATUS);
        }

        couponList.forEach(coupon -> coupon.updateStatus(status, customUserDetails.getId()));

        couponRepository.saveAll(couponList);
        
        return couponList.stream()
                .map(coupon -> CouponSearchResponse.builder()
                        .couponId(coupon.getId())
                        .couponName(coupon.getCouponName())
                        .registeredAt(coupon.getCreatedAt().toLocalDate())
                        .couponType(coupon.getCouponType())
                        .expireDate(formatExpireDate(coupon))
                        .discountRate(getDiscountRateText(coupon))
                        .issuedMethod(coupon.getIssuedMethod())
                        .issuedStatus(coupon.getIssuedStatus())
                        .description(coupon.getDescription())
                        .build()
                ).collect(Collectors.toList());
    }



}
