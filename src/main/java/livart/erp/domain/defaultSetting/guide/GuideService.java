package livart.erp.domain.defaultSetting.guide;

import livart.common.Auth.CustomUserDetails;
import livart.common.domain.setting.entity.Guide;
import livart.common.domain.setting.entity.GuideImage;
import livart.common.domain.setting.repository.GuideImageRepository;
import livart.common.domain.setting.repository.GuideRepository;
import livart.common.dto.enums.defaultSetting.GuideType;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.service.GlobalService;
import livart.erp.domain.defaultSetting.guide.dto.request.GuideRequest;
import livart.erp.domain.defaultSetting.guide.dto.request.ImageDto;
import livart.erp.domain.defaultSetting.guide.dto.response.GuideResponse;
import livart.erp.domain.defaultSetting.guide.dto.request.UseWithRequest;
import livart.erp.domain.defaultSetting.guide.dto.response.UseWithResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuideService {

    private final GlobalService globalService;
    private final GuideRepository guideRepository;

    public List<UseWithResponse> getUseWith(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        Guide use = guideRepository.findGuideByType(GuideType.USE).orElse(null);
        Guide withdraw = guideRepository.findGuideByType(GuideType.WITHDRAW).orElse(null);

        List<UseWithResponse> responses = new ArrayList<>();

        if (use != null) {
            responses.add(UseWithResponse.builder()
                    .guideId(use.getId())
                    .type(use.getType())
                    .content(use.getContent())
                    .build());
        }

        if (withdraw != null) {
            responses.add(UseWithResponse.builder()
                    .guideId(withdraw.getId())
                    .type(withdraw.getType())
                    .content(withdraw.getContent())
                    .build());
        }

        return responses;
    }

    @Transactional
    public List<UseWithResponse> updateUseWith(CustomUserDetails customUserDetails, UseWithRequest request){
        globalService.validateAdmin(customUserDetails);

        Guide use = guideRepository.findGuideByType(GuideType.USE)
                .map(g -> {
                    g.updateContent(request.getUseGuide(), customUserDetails.getId());
                    return g;
                })
                .orElseGet(() -> Guide.builder()
                        .type(GuideType.USE)
                        .content(request.getUseGuide())
                        .updateBy(customUserDetails.getId())
                        .build());

        Guide withdraw = guideRepository.findGuideByType(GuideType.WITHDRAW)
                .map(g -> {
                    g.updateContent(request.getWithdrawGuide(), customUserDetails.getId());
                    return g;
                })
                .orElseGet(() -> Guide.builder()
                        .type(GuideType.WITHDRAW)
                        .content(request.getWithdrawGuide())
                        .updateBy(customUserDetails.getId())
                        .build());

        Guide useSaved = guideRepository.save(use);
        Guide withSaved = guideRepository.save(withdraw);

        UseWithResponse useResponse = UseWithResponse.builder()
                .guideId(useSaved.getId())
                .type(useSaved.getType())
                .content(useSaved.getContent())
                .build();

        UseWithResponse withResponse = UseWithResponse.builder()
                .guideId(withSaved.getId())
                .type(withSaved.getType())
                .content(withSaved.getContent())
                .build();

        List<UseWithResponse> responses = List.of(useResponse,withResponse);

        return responses;
    }

    public GuideResponse getGuide(CustomUserDetails customUserDetails, String type){
        globalService.validateAdmin(customUserDetails);

        if(!GuideType.contains(type)){
            throw new CustomException(ErrorCode.INVALID_TYPE);
        }

        GuideType guideType = GuideType.valueOf(type.toUpperCase());

        Guide guide = guideRepository.findGuideByType(guideType)
                .orElseThrow(() -> new CustomException(ErrorCode.GUIDE_NOT_FOUND));

        List<ImageDto> imageDtoList = guide.getGuideImages().stream()
                .sorted(Comparator.comparing(GuideImage::getOrderIndex))
                .map(i -> ImageDto.builder()
                        .orderIndex(i.getOrderIndex())
                        .imageUrl(i.getImageUrl())
                        .fileName(i.getFileName())
                        .build()
                ).collect(Collectors.toList());

        return GuideResponse.builder()
                .guideId(guide.getId())
                .type(guide.getType())
                .content(guide.getContent())
                .imageList(imageDtoList)
                .build();
    }

    @Transactional
    public GuideResponse updateGuide(CustomUserDetails customUserDetails, String type, GuideRequest request){
        globalService.validateAdmin(customUserDetails);

        if(!GuideType.contains(type)){
            throw new CustomException(ErrorCode.INVALID_TYPE);
        }

        GuideType guideType = GuideType.valueOf(type.toUpperCase());

        Guide guide = guideRepository.findGuideByType(guideType)
                .map(g -> {
                    g.updateContent(request.getContent(), customUserDetails.getId());
                    g.getGuideImages().clear();
                    return g;
                })
                .orElseGet(() -> Guide.builder()
                        .type(guideType)
                        .content(request.getContent())
                        .updateBy(customUserDetails.getId())
                        .build());

        Guide saved1 = guideRepository.save(guide);

        List<GuideImage> images = request.getImageList().stream()
                .sorted(Comparator.comparing(ImageDto::getOrderIndex))
                .map(i -> GuideImage.builder()
                        .orderIndex(i.getOrderIndex())
                        .fileName(i.getFileName())
                        .imageUrl(i.getImageUrl())
                        .updatedBy(customUserDetails.getId())
                        .guide(saved1)
                        .build()
                ).collect(Collectors.toList());

        saved1.getGuideImages().addAll(images);
        Guide saved = guideRepository.save(saved1);

        List<ImageDto> imageDtoList = saved.getGuideImages().stream()
                .sorted(Comparator.comparing(GuideImage::getOrderIndex))
                .map(i -> ImageDto.builder()
                        .orderIndex(i.getOrderIndex())
                        .imageUrl(i.getImageUrl())
                        .fileName(i.getFileName())
                        .build()
                ).collect(Collectors.toList());

        return GuideResponse.builder()
                .guideId(saved.getId())
                .type(saved.getType())
                .content(saved.getContent())
                .imageList(imageDtoList)
                .build();
    }
}
