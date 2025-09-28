package livart.erp.domain.design;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import livart.common.Auth.CustomUserDetails;
import livart.common.domain.design.entity.*;
import livart.common.domain.design.repository.*;
import livart.common.domain.setting.entity.OperatingHours;
import livart.common.domain.setting.repository.OperatingHoursRepository;
import livart.common.dto.enums.defaultSetting.DayType;
import livart.common.dto.enums.defaultSetting.OperatingHoursType;
import livart.common.dto.request.PopupRegisterRequest;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.mapper.SearchResult;
import livart.common.service.GlobalService;
import livart.common.dto.enums.design.PopupStatus;
import livart.erp.domain.design.dto.request.*;
import livart.erp.domain.design.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.*;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DesignService {
    private final GlobalService globalService;
    private final BrandRepository brandRepository;
    private final ProductBannerRepository productBannerRepository;
    private final PopupRepository popupRepository;
    private final InteriorsInfoRepository interiorsInfoRepository;
    private final InteriorsImageRepository interiorsImageRepository;
    private final MainBannerRepository mainBannerRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
    public BrandResponse saveBrandInfo(CustomUserDetails customUserDetails, BrandRequest request){
        globalService.validateAdmin(customUserDetails);

        Brand brand = brandRepository.findById(1L)
                .map(existing -> {
                    existing.update(request.getFileName(), request.getImageUrl());
                    return existing;
                })
                .orElseGet(() -> Brand.builder()
                        .fileName(request.getFileName())
                        .imageUrl(request.getImageUrl())
                        .updatedBy(customUserDetails.getId())
                        .build());

        Brand saved = brandRepository.save(brand);

        return BrandResponse.builder()
                .fileName(request.getFileName())
                .imageUrl(saved.getImageUrl())
                .build();
    }

    public BrandResponse getBrandInfo(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        Brand brand = brandRepository.findById(1L).orElseThrow(() -> new CustomException(ErrorCode.BRAND_INFO_NOT_FOUND));

        return BrandResponse.builder()
                .fileName(brand.getFileName())
                .imageUrl(brand.getImageUrl())
                .build();
    }

    @Transactional
    public List<ProductBannerResponse> saveProductBannerInfo(CustomUserDetails customUserDetails, List<ImageListDto> request){
        globalService.validateAdmin(customUserDetails);

        productBannerRepository.deleteAll();

        checkDuplicate(request);

        List<ProductBanner> bannerList = request.stream()
                .sorted(Comparator.comparing(ImageListDto::getOrderIndex))
                .map(image -> ProductBanner.builder()
                                .fileName(image.getFileName())
                                .imageUrl(image.getImageUrl())
                                .linkUrl(image.getLinkUrl())
                                .orderIndex(image.getOrderIndex())
                                .createdBy(customUserDetails.getId())
                                .build()
                ).collect(Collectors.toList());

        List<ProductBannerResponse> savedList = productBannerRepository.saveAll(bannerList).stream()
                .sorted(Comparator.comparing(ProductBanner::getOrderIndex))
                .map(banner -> ProductBannerResponse.builder()
                        .fileName(banner.getFileName())
                        .imageUrl(banner.getImageUrl())
                        .linkUrl(banner.getLinkUrl())
                        .orderIndex(banner.getOrderIndex())
                        .build()
                ).collect(Collectors.toList());

        return savedList;
    }

    public List<ProductBannerResponse> getProductBannerInfo(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        List<ProductBannerResponse> bannerList = productBannerRepository.findAll().stream()
                .sorted(Comparator.comparing(ProductBanner::getOrderIndex))
                .map(banner -> ProductBannerResponse.builder()
                        .fileName(banner.getFileName())
                        .imageUrl(banner.getImageUrl())
                        .linkUrl(banner.getLinkUrl())
                        .orderIndex(banner.getOrderIndex())
                        .build()
                ).collect(Collectors.toList());

        return bannerList;
    }

    @Transactional

    public PopupResponse savePopupInfo(CustomUserDetails customUserDetails, PopupRegisterRequest request){
        globalService.validateAdmin(customUserDetails);

        Popup popup = Popup.builder()
                .title(request.getTitle())
                .status(request.getStatus())
                .popupType(request.getPopupType())
                .topLocationPixel(request.getTopLocationPixel())
                .leftLocationPixel(request.getLeftLocationPixel())
                .isHiddenToday(request.getIsHiddenToday())
                .widthPixel(request.getWidthPixel())
                .heightPixel(request.getHeightPixel())
                .pageUrl(request.getPageUrl())
                .parameter(request.getParameter())
                .createdUserId(customUserDetails.getId())
                .exposedStartDate(request.getStart())
                .exposedEndDate(request.getEnd())
                .build();

        Popup saved = popupRepository.save(popup);

        return PopupResponse.builder()
                .popupId(saved.getId())
                .title(saved.getTitle())
                .status(saved.getStatus())
                .popupType(saved.getPopupType())
                .topLocationPixel(saved.getTopLocationPixel())
                .leftLocationPixel(saved.getLeftLocationPixel())
                .isHiddenToday(saved.getIsHiddenToday())
                .widthPixel(saved.getWidthPixel())
                .heightPixel(saved.getHeightPixel())
                .pageUrl(saved.getPageUrl())
                .parameter(saved.getParameter())
                .exposedStartDate(saved.getExposedStartDate().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .exposedEndDate(popup.getExposedEndDate().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .build();
    }

    public PopupResponse getPopupInfo(CustomUserDetails customUserDetails, Long popupId){
        globalService.validateAdmin(customUserDetails);
        
        Popup popup = popupRepository.findById(popupId).orElseThrow(() -> new CustomException(ErrorCode.POPUP_NOT_FOUND));

        return PopupResponse.builder()
                .popupId(popup.getId())
                .title(popup.getTitle())
                .status(popup.getStatus())
                .popupType(popup.getPopupType())
                .topLocationPixel(popup.getTopLocationPixel())
                .leftLocationPixel(popup.getLeftLocationPixel())
                .isHiddenToday(popup.getIsHiddenToday())
                .widthPixel(popup.getWidthPixel())
                .heightPixel(popup.getHeightPixel())
                .pageUrl(popup.getPageUrl())
                .parameter(popup.getParameter())
                .exposedStartDate(popup.getExposedStartDate().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .exposedEndDate(popup.getExposedEndDate().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .build();
    }

    @Transactional
    public PopupResponse updatePopupInfo(CustomUserDetails customUserDetails, PopupRegisterRequest request, Long popupId){
        globalService.validateAdmin(customUserDetails);

        Popup popup = popupRepository.findById(popupId).orElseThrow(() -> new CustomException(ErrorCode.POPUP_NOT_FOUND));

        popup.update(request, customUserDetails.getId());
        Popup saved = popupRepository.save(popup);

        return PopupResponse.builder()
                .popupId(saved.getId())
                .title(saved.getTitle())
                .status(saved.getStatus())
                .popupType(saved.getPopupType())
                .topLocationPixel(saved.getTopLocationPixel())
                .leftLocationPixel(saved.getLeftLocationPixel())
                .isHiddenToday(saved.getIsHiddenToday())
                .widthPixel(saved.getWidthPixel())
                .heightPixel(saved.getHeightPixel())
                .pageUrl(saved.getPageUrl())
                .parameter(saved.getParameter())
                .exposedStartDate(saved.getExposedStartDate().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .exposedEndDate(popup.getExposedEndDate().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .build();
    }

    public SearchResult<PopupResponse> searchPopupList(CustomUserDetails customUserDetails, PopupSearchRequest request, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        QPopup popup = QPopup.popup;
        BooleanBuilder builder = new BooleanBuilder();

        if(StringUtils.hasText(request.getKeyword()) && request.getKey() != null){
            switch (request.getKey()){
                case TITLE -> builder.and(popup.title.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keywordBuilder = new BooleanBuilder();
                    keywordBuilder.or(popup.title.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(popup.parameter.containsIgnoreCase(request.getKeyword()));
                    builder.and(keywordBuilder);
                }
            }
        }

        if(request.getRegisterDate() != null){
            if(request.getRegisterDate().getStartDate() != null){
                builder.and(popup.createdAt.goe(request.getRegisterDate().getStartDate().atStartOfDay()));
            }
            if(request.getRegisterDate().getEndDate() != null){
                builder.and(popup.createdAt.loe(request.getRegisterDate().getEndDate().atTime(23,59,59)));
            }
        }

        if(request.getStatus() != null && request.getStatus() != PopupStatus.ALL){
            builder.and(popup.status.eq(request.getStatus()));
        }

        if(request.getType() != null){
            builder.and(popup.popupType.eq(request.getType()));
        }

        List<Popup> popupList = jpaQueryFactory
                .selectFrom(popup)
                .where(builder)
                .orderBy(popup.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = jpaQueryFactory
                .select(popup.count())
                .from(popup)
                .where(builder)
                .fetchOne();

        List<PopupResponse> results = popupList.stream()
                .map(p -> PopupResponse.builder()
                            .popupId(p.getId())
                            .title(p.getTitle())
                            .status(p.getStatus())
                            .popupType(p.getPopupType())
                            .topLocationPixel(p.getTopLocationPixel())
                            .leftLocationPixel(p.getLeftLocationPixel())
                            .isHiddenToday(p.getIsHiddenToday())
                            .widthPixel(p.getWidthPixel())
                            .heightPixel(p.getHeightPixel())
                            .pageUrl(p.getPageUrl())
                            .parameter(p.getParameter())
                            .createdAt(p.getCreatedAt().toLocalDate())
                            .updatedAt(p.getUpdatedAt().toLocalDate())
                            .exposedStartDate(p.getExposedStartDate())
                            .exposedEndDate(p.getExposedEndDate())
                            .build()
                ).collect(Collectors.toList());

        return SearchResult.<PopupResponse>builder()
                .totalCount(totalCount)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(results)
                .build();
    }


    @Transactional
    public void deletePopupList(CustomUserDetails customUserDetails, List<Long> popupIdList){
        globalService.validateAdmin(customUserDetails);

        List<Popup> popupList = popupRepository.findAllById(popupIdList);

        if (popupList.size() != popupIdList.size()) {
            throw new CustomException(ErrorCode.POPUP_NOT_FOUND); // 일부 누락되었을 경우
        }

        popupRepository.deleteAll(popupList);

    }

    @Transactional
    public InteriorInfoResponse saveInteriorInfo(CustomUserDetails customUserDetails, InteriorInfoRequest request){
        globalService.validateAdmin(customUserDetails);

        InteriorsInfo info = interiorsInfoRepository.findById(1L)
                .map(existing -> {
                    existing.update(
                            request.getEmail(),
                            request.getFaxNum(),
                            request.getCounselNum(),
                            request.getDirections(),
                            request.getUsageGuide(),
                            customUserDetails.getId());
                    return existing;
                })
                .orElseGet(() -> InteriorsInfo.builder()
                        .email(request.getEmail())
                        .faxNum(request.getFaxNum())
                        .counselNum(request.getCounselNum())
                        .directions(request.getDirections())
                        .usageGuide(request.getUsageGuide())
                        .createdBy(customUserDetails.getId())
                        .build());

        operatingHoursRepository.deleteByOperatingHoursType(OperatingHoursType.INTERIOR_INFO);

        List<OperatingHours> operatingHours = request.getHours().entrySet().stream()
                .map(entry -> {
                    DayType dayType = entry.getKey();
                    InteriorInfoRequest.TimeRange timeRange = entry.getValue();

                    return OperatingHours.builder()
                            .operatingHoursType(OperatingHoursType.INTERIOR_INFO)
                            .dayType(dayType)
                            .startTime(timeRange.getStart())
                            .endTime(timeRange.getEnd())
                            .updatedBy(customUserDetails.getId())
                            .build();
                }).collect(Collectors.toList());

        Map<DayType, InteriorInfoResponse.TimeRange> hours = operatingHoursRepository.saveAll(operatingHours).stream()
                .collect(Collectors.toMap(
                        OperatingHours::getDayType,
                        bh -> InteriorInfoResponse.TimeRange.builder()
                                .start(bh.getStartTime())
                                .end(bh.getEndTime())
                                .build(),
                        (existing, replacement) -> existing, // 중복 방지
                        () -> new EnumMap<>(DayType.class)
                ));

        InteriorsInfo savedInfo = interiorsInfoRepository.save(info);

        interiorsImageRepository.deleteAll();

        checkDuplicate(request.getImageList());

        List<InteriorsImage> imageList = request.getImageList()
                .stream()
                .sorted(Comparator.comparing(ImageListDto::getOrderIndex))
                .map(dto -> InteriorsImage.builder()
                        .fileName(dto.getFileName())
                        .imageUrl(dto.getImageUrl())
                        .orderIndex(dto.getOrderIndex())
                        .createdBy(customUserDetails.getId())
                        .build()
                ).collect(Collectors.toList());

        List<InteriorsImage> savedImage = interiorsImageRepository.saveAll(imageList);

        List<ImageListDto> imageLists = savedImage.stream()
                .sorted(Comparator.comparing(InteriorsImage::getOrderIndex))
                .map(image -> ImageListDto.builder()
                        .fileName(image.getFileName())
                        .imageUrl(image.getImageUrl())
                        .orderIndex(image.getOrderIndex())
                        .build()
                ).collect(Collectors.toList());

        return InteriorInfoResponse.builder()
                .email(savedInfo.getEmail())
                .faxNum(savedInfo.getFaxNum())
                .counselNum(savedInfo.getCounselNum())
                .directions(savedInfo.getDirections())
                .usageGuide(savedInfo.getUsageGuide())
                .hours(hours)
                .imageList(imageLists)
                .build();

    }

    public InteriorInfoResponse getInteriorInfo(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        List<InteriorsImage> imageList = interiorsImageRepository.findAll();
        InteriorsInfo info = interiorsInfoRepository.findById(1L).orElseThrow(() -> new CustomException(ErrorCode.INTERIOR_INFO_NOT_FOUND));

        List<ImageListDto> imageLists = imageList.stream()
                .sorted(Comparator.comparing(InteriorsImage::getOrderIndex))
                .map(image -> ImageListDto.builder()
                        .fileName(image.getFileName())
                        .imageUrl(image.getImageUrl())
                        .orderIndex(image.getOrderIndex())
                        .build()).collect(Collectors.toList());

        Map<DayType, InteriorInfoResponse.TimeRange> hours = operatingHoursRepository.findByOperatingHoursType(OperatingHoursType.INTERIOR_INFO).stream()
                .collect(Collectors.toMap(
                        OperatingHours::getDayType,
                        bh -> InteriorInfoResponse.TimeRange.builder()
                                .start(bh.getStartTime())
                                .end(bh.getEndTime())
                                .build(),
                        (existing, replacement) -> existing, // 중복 방지
                        () -> new EnumMap<>(DayType.class)
                ));

        return InteriorInfoResponse.builder()
                .email(info.getEmail())
                .faxNum(info.getFaxNum())
                .counselNum(info.getCounselNum())
                .directions(info.getDirections())
                .usageGuide(info.getUsageGuide())
                .hours(hours)
                .imageList(imageLists)
                .build();
    }

    @Transactional
    public List<MainBannerResponse> saveMainBannerInfo(CustomUserDetails customUserDetails, List<ImageListDto> request){
        globalService.validateAdmin(customUserDetails);

        mainBannerRepository.deleteAll();
        checkDuplicate(request);

        List<MainBanner> bannerList = request.stream()
                .sorted(Comparator.comparing(ImageListDto::getOrderIndex))
                .map(image -> MainBanner.builder()
                        .fileName(image.getFileName())
                        .imageUrl(image.getImageUrl())
                        .linkUrl(image.getLinkUrl())
                        .orderIndex(image.getOrderIndex())
                        .createdBy(customUserDetails.getId())
                        .build()
                ).collect(Collectors.toList());

        List<MainBannerResponse> savedList = mainBannerRepository.saveAll(bannerList).stream()
                .sorted(Comparator.comparing(MainBanner::getOrderIndex))
                .map(banner -> MainBannerResponse.builder()
                        .fileName(banner.getFileName())
                        .imageUrl(banner.getImageUrl())
                        .linkUrl(banner.getLinkUrl())
                        .orderIndex(banner.getOrderIndex())
                        .build()
                ).collect(Collectors.toList());

        return savedList;
    }

    public List<MainBannerResponse> getMainBannerInfo(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        List<MainBannerResponse> bannerList = mainBannerRepository.findAll().stream()
                .sorted(Comparator.comparing(MainBanner::getOrderIndex))
                .map(banner -> MainBannerResponse.builder()
                        .fileName(banner.getFileName())
                        .imageUrl(banner.getImageUrl())
                        .linkUrl(banner.getLinkUrl())
                        .orderIndex(banner.getOrderIndex())
                        .build()
                ).collect(Collectors.toList());
        return bannerList;
    }

    public void checkDuplicate(List<ImageListDto> request){
        if(request.size() > 5 || request.size() < 0){
            throw new CustomException(ErrorCode.INVALID_IMAGE_SIZE);
        }

        boolean hasDuplicate = request.stream()
                .map(ImageListDto::getOrderIndex)
                .collect(Collectors.toSet())
                .size() != request.size();

        if (hasDuplicate) {
            throw new CustomException(ErrorCode.DUPLICATE_ORDER_VALUE);
        }
    }
}
