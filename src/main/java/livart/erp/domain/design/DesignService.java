package livart.erp.domain.design;

import livart.common.Auth.CustomUserDetails;
import livart.common.domain.design.entity.*;
import livart.common.domain.design.repository.*;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.mapper.SearchResult;
import livart.common.service.GlobalService;
import livart.common.dto.enums.PopupStatus;
import livart.erp.domain.design.dto.request.*;
import livart.erp.domain.design.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;
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
                .updatedBy(saved.getUpdatedBy())
                .build();
    }

    public BrandResponse getBrandInfo(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        Brand brand = brandRepository.findById(1L).orElseThrow(() -> new CustomException(ErrorCode.BRAND_INFO_NOT_FOUND));

        return BrandResponse.builder()
                .fileName(brand.getFileName())
                .imageUrl(brand.getImageUrl())
                .updatedBy(brand.getUpdatedBy())
                .build();
    }

    @Transactional
    public List<ProductBannerResponse> saveProductBannerInfo(CustomUserDetails customUserDetails, List<ImageListDto> request){
        globalService.validateAdmin(customUserDetails);

        brandRepository.deleteAll();

        checkDuplicate(request);

        List<ProductBanner> bannerList = request.stream()
                .map(image -> ProductBanner.builder()
                                .fileName(image.getFileName())
                                .imageUrl(image.getImageUrl())
                                .orderIndex(image.getOrderIndex())
                                .createdBy(customUserDetails.getId())
                                .build()
                ).collect(Collectors.toList());

        List<ProductBannerResponse> savedList = productBannerRepository.saveAll(bannerList).stream()
                .map(banner -> ProductBannerResponse.builder()
                        .fileName(banner.getFileName())
                        .imageUrl(banner.getImageUrl())
                        .orderIndex(banner.getOrderIndex())
                        .build()
                ).collect(Collectors.toList());



        return savedList;
    }

    public List<ProductBannerResponse> getProductBannerInfo(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        List<ProductBannerResponse> bannerList = productBannerRepository.findAll().stream()
                .map(banner -> ProductBannerResponse.builder()
                        .fileName(banner.getFileName())
                        .imageUrl(banner.getImageUrl())
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
                .exposedStartDate(request.getStart().toInstant(ZoneOffset.UTC))
                .exposedEndDate(request.getEnd().toInstant(ZoneOffset.UTC))
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

    public SearchResult<PopupResponse> searchPopupList(CustomUserDetails customUserDetails, PopupSearchRequest request){
        globalService.validateAdmin(customUserDetails);

        LocalDateTime startDateTime = request.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = request.getEndDate().atTime(LocalTime.MAX);

        Instant startInstant = startDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant(); // 2024-04-19T15:00:00Z
        Instant endInstant = endDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant();

        List<PopupResponse> popupList = popupRepository
                .findPopupsByTitleAndTypeAndExposure(request.getTitle(),request.getStatus(), request.getType(), startInstant, endInstant)
                .stream()
                .map(popup -> PopupResponse.builder()
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
                        .build()
                ).collect(Collectors.toList());

        Long fullCount = popupRepository.count();
        Long totalCount = (long) popupList.size();

        return SearchResult.<PopupResponse>builder()
                .fullCount(fullCount)
                .totalCount(totalCount)
                .data(popupList)
                .build();
    }

    @Transactional
    public List<PopupDeleteResponse> deletePopupList(CustomUserDetails customUserDetails, List<Long> popupIdList){
        globalService.validateAdmin(customUserDetails);

        List<Popup> popupList = popupRepository.findAllById(popupIdList);

        if (popupList.size() != popupIdList.size()) {
            throw new CustomException(ErrorCode.POPUP_NOT_FOUND); // 일부 누락되었을 경우
        }

        popupList.forEach(popup -> popup.updateStatus(PopupStatus.DELETED));

        popupRepository.saveAll(popupList);

        return popupList.stream()
                .map(popup -> PopupDeleteResponse.builder()
                        .popId(popup.getId())
                        .title(popup.getTitle())
                        .status(popup.getStatus())
                        .build()
                ).collect(Collectors.toList());
    }

    @Transactional
    public InteriorInfoResponse saveInteriorInfo(CustomUserDetails customUserDetails, InteriorInfoRequest request){
        globalService.validateAdmin(customUserDetails);

        InteriorsInfo info = interiorsInfoRepository.findById(1L)
                .map(existing -> {
                    existing.update(
                            request.getEmail(),
                            request.getPaxNum(),
                            request.getDirections(),
                            request.getUsageGuide(),
                            request.getOperatingHours(),
                            customUserDetails.getId());
                    return existing;
                })
                .orElseGet(() -> InteriorsInfo.builder()
                        .email(request.getEmail())
                        .paxNum(request.getPaxNum())
                        .directions(request.getDirections())
                        .usageGuide(request.getUsageGuide())
                        .operatingHours(request.getOperatingHours())
                        .createdBy(customUserDetails.getId())
                        .build());

        InteriorsInfo savedInfo = interiorsInfoRepository.save(info);

        interiorsImageRepository.deleteAll();

        checkDuplicate(request.getImageList());

        List<InteriorsImage> imageList = request.getImageList()
                .stream()
                .map(dto -> InteriorsImage.builder()
                        .fileName(dto.getFileName())
                        .imageUrl(dto.getImageUrl())
                        .orderIndex(dto.getOrderIndex())
                        .createdBy(customUserDetails.getId())
                        .build()).collect(Collectors.toList());

        List<InteriorsImage> savedImage = interiorsImageRepository.saveAll(imageList);

        List<ImageListDto> imageLists = savedImage.stream()
                .map(image -> ImageListDto.builder()
                        .fileName(image.getFileName())
                        .imageUrl(image.getImageUrl())
                        .orderIndex(image.getOrderIndex())
                        .build()).collect(Collectors.toList());

        return InteriorInfoResponse.builder()
                .email(savedInfo.getEmail())
                .paxNum(savedInfo.getPaxNum())
                .directions(savedInfo.getDirections())
                .usageGuide(savedInfo.getUsageGuide())
                .operatingHours(savedInfo.getOperatingHours())
                .imageList(imageLists)
                .build();

    }

    public InteriorInfoResponse getInteriorInfo(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        List<InteriorsImage> imageList = interiorsImageRepository.findAll();
        InteriorsInfo info = interiorsInfoRepository.findById(1L).orElseThrow(() -> new CustomException(ErrorCode.INTERIOR_INFO_NOT_FOUND));

        List<ImageListDto> imageLists = imageList.stream()
                .map(image -> ImageListDto.builder()
                        .fileName(image.getFileName())
                        .imageUrl(image.getImageUrl())
                        .orderIndex(image.getOrderIndex())
                        .build()).collect(Collectors.toList());

        return InteriorInfoResponse.builder()
                .email(info.getEmail())
                .paxNum(info.getPaxNum())
                .directions(info.getDirections())
                .usageGuide(info.getUsageGuide())
                .operatingHours(info.getOperatingHours())
                .imageList(imageLists)
                .build();
    }

    @Transactional
    public List<MainBannerResponse> saveMainBannerInfo(CustomUserDetails customUserDetails, List<ImageListDto> request){
        globalService.validateAdmin(customUserDetails);

        mainBannerRepository.deleteAll();
        checkDuplicate(request);

        List<MainBanner> bannerList = request.stream()
                .map(image -> MainBanner.builder()
                        .fileName(image.getFileName())
                        .imageUrl(image.getImageUrl())
                        .orderIndex(image.getOrderIndex())
                        .createdBy(customUserDetails.getId())
                        .build()
                ).collect(Collectors.toList());

        List<MainBannerResponse> savedList = mainBannerRepository.saveAll(bannerList).stream()
                .map(banner -> MainBannerResponse.builder()
                        .fileName(banner.getFileName())
                        .imageUrl(banner.getImageUrl())
                        .orderIndex(banner.getOrderIndex())
                        .build()
                ).collect(Collectors.toList());

        return savedList;
    }

    public List<MainBannerResponse> getMainBannerInfo(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        List<MainBannerResponse> bannerList = mainBannerRepository.findAll().stream()
                .map(banner -> MainBannerResponse.builder()
                        .fileName(banner.getFileName())
                        .imageUrl(banner.getImageUrl())
                        .orderIndex(banner.getOrderIndex())
                        .build()
                ).collect(Collectors.toList());
        return bannerList;
    }

    public void checkDuplicate(List<ImageListDto> request){
        boolean hasDuplicate = request.stream()
                .map(ImageListDto::getOrderIndex)
                .collect(Collectors.toSet())
                .size() != request.size();

        if (hasDuplicate) {
            throw new CustomException(ErrorCode.DUPLICATE_ORDER_VALUE);
        }
    }
}
