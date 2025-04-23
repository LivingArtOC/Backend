package livart.erp.domain.promotion;

import livart.common.Auth.CustomUserDetails;
import livart.common.domain.promotion.entity.Catalog;
import livart.common.domain.promotion.entity.CouponSetting;
import livart.common.domain.promotion.repository.CatalogRepository;
import livart.common.domain.promotion.repository.CouponSettingRepository;
import livart.common.dto.enums.CatalogType;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.service.GlobalService;
import livart.erp.domain.promotion.dto.request.CatalogRequest;
import livart.erp.domain.promotion.dto.request.CouponSettingRequest;
import livart.erp.domain.promotion.dto.response.CatalogResponse;
import livart.erp.domain.promotion.dto.response.CouponSettingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PromotionService {
    private final GlobalService globalService;
    private final CatalogRepository catalogRepository;
    private final CouponSettingRepository couponSettingRepository;
    
    @Transactional
    public List<CatalogResponse> saveCatalog(CustomUserDetails customUserDetails, CatalogRequest request){
        globalService.validateAdmin(customUserDetails);

        Catalog haum = catalogRepository.findCatalogByCatalogType(CatalogType.HAUM)
                .map(existing -> {
                    existing.update(request.getHaumFileUrl(), customUserDetails.getId());
                    return existing;
                })
                .orElseGet(() -> Catalog.builder()
                        .catalogType(CatalogType.HAUM)
                        .fileUrl(request.getHaumFileUrl())
                        .createdBy(customUserDetails.getId())
                        .build());

        Catalog office = catalogRepository.findCatalogByCatalogType(CatalogType.OFFICE)
                .map(existing -> {
                    existing.update(request.getOfficeFileUrl(), customUserDetails.getId());
                    return existing;
                })
                .orElseGet(() -> Catalog.builder()
                        .catalogType(CatalogType.OFFICE)
                        .fileUrl(request.getOfficeFileUrl())
                        .createdBy(customUserDetails.getId())
                        .build());
        
        Catalog savedHaum = catalogRepository.save(haum);
        Catalog savedOffice = catalogRepository.save(office);
        
        CatalogResponse haumResponse = CatalogResponse.builder()
                .catalogId(savedHaum.getId())
                .catalogType(savedHaum.getCatalogType())
                .fileUrl(savedHaum.getFileUrl())
                .build();

        CatalogResponse officeResponse = CatalogResponse.builder()
                .catalogId(savedOffice.getId())
                .catalogType(savedOffice.getCatalogType())
                .fileUrl(savedOffice.getFileUrl())
                .build();

        return List.of(haumResponse, officeResponse);
    }

    public List<CatalogResponse> getCatalog(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        Catalog haum = catalogRepository.findCatalogByCatalogType(CatalogType.HAUM).orElseThrow(() -> new CustomException(ErrorCode.CATALOG_NOT_FOUND));
        Catalog office = catalogRepository.findCatalogByCatalogType(CatalogType.OFFICE).orElseThrow(() -> new CustomException(ErrorCode.CATALOG_NOT_FOUND));

        CatalogResponse haumResponse = CatalogResponse.builder()
                .catalogId(haum.getId())
                .catalogType(haum.getCatalogType())
                .fileUrl(haum.getFileUrl())
                .build();

        CatalogResponse officeResponse = CatalogResponse.builder()
                .catalogId(office.getId())
                .catalogType(office.getCatalogType())
                .fileUrl(office.getFileUrl())
                .build();

        return List.of(haumResponse, officeResponse);

    }

    @Transactional
    public CouponSettingResponse saveCouponSetting(CustomUserDetails customUserDetails, CouponSettingRequest request){
        globalService.validateAdmin(customUserDetails);

        CouponSetting couponSetting = couponSettingRepository.findById(1L)
                .map(existing -> {
                    existing.update(request.getPurchaseStandard(), request.getRestoreCoupon(), customUserDetails.getId());
                    return existing;
                })
                .orElseGet(() -> CouponSetting.builder()
                        .standard(request.getPurchaseStandard())
                        .restoreCoupon(request.getRestoreCoupon())
                        .createdBy(customUserDetails.getId())
                        .build());

        CouponSetting saved = couponSettingRepository.save(couponSetting);

        return CouponSettingResponse.builder()
                .settingId(saved.getId())
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
                .purchaseStandard(couponSetting.getStandard())
                .restoreCoupon(couponSetting.getRestoreCoupon())
                .build();
    }
}
