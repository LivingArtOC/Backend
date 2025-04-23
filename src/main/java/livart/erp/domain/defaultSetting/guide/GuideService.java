package livart.erp.domain.defaultSetting.guide;

import livart.common.Auth.CustomUserDetails;
import livart.common.domain.setting.entity.Guide;
import livart.common.domain.setting.repository.GuideRepository;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.service.GlobalService;
import livart.erp.domain.defaultSetting.guide.dto.request.GuideRequest;
import livart.erp.domain.defaultSetting.guide.dto.response.GuideResponse;
import livart.erp.domain.defaultSetting.policy.dto.request.UseWithRequest;
import livart.erp.domain.defaultSetting.policy.dto.response.UseWithResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuideService {

    private final GlobalService globalService;
    private final GuideRepository guideRepository;

    @Transactional
    public List<UseWithResponse>  saveUseWith(CustomUserDetails customUserDetails, UseWithRequest request){
        globalService.validateAdmin(customUserDetails);

        Guide useGuide = Guide.builder()
                .title("use")
                .content(request.getUseGuide())
                .imageUrl(null)
                .updateBy(customUserDetails.getId())
                .build();

        Guide withdrawGuide = Guide.builder()
                .title("withdraw")
                .content(request.getWithdrawGuide())
                .imageUrl(null)
                .updateBy(customUserDetails.getId())
                .build();

        Guide useSaved = guideRepository.save(useGuide);
        Guide withSaved = guideRepository.save(withdrawGuide);

        UseWithResponse useResponse = UseWithResponse.builder()
                .guideId(useSaved.getId())
                .content(useSaved.getContent())
                .build();

        UseWithResponse withResponse = UseWithResponse.builder()
                .guideId(withSaved.getId())
                .content(withSaved.getContent())
                .build();

        List<UseWithResponse> responses = List.of(useResponse,withResponse);

        return responses;
    }

    public List<UseWithResponse> getUseWith(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        Guide use = guideRepository.findGuideByTitle("use")
                .orElseThrow(() -> new CustomException(ErrorCode.GUIDE_NOT_FOUND));
        Guide withdraw = guideRepository.findGuideByTitle("withdraw")
                .orElseThrow(() -> new CustomException(ErrorCode.GUIDE_NOT_FOUND));

        UseWithResponse useResponse = UseWithResponse.builder()
                .guideId(use.getId())
                .content(use.getContent())
                .build();

        UseWithResponse withResponse = UseWithResponse.builder()
                .guideId(withdraw.getId())
                .content(withdraw.getContent())
                .build();

        List<UseWithResponse> responses = List.of(useResponse,withResponse);

        return responses;
    }

    @Transactional
    public List<UseWithResponse> updateUseWith(CustomUserDetails customUserDetails, UseWithRequest request){
        globalService.validateAdmin(customUserDetails);

        Guide use = guideRepository.findGuideByTitle("use")
                .orElseThrow(() -> new CustomException(ErrorCode.GUIDE_NOT_FOUND));
        Guide withdraw = guideRepository.findGuideByTitle("withdraw")
                .orElseThrow(() -> new CustomException(ErrorCode.GUIDE_NOT_FOUND));

        use.updateContent(request.getUseGuide());
        withdraw.updateContent(request.getWithdrawGuide());

        Guide useSaved = guideRepository.save(use);
        Guide withSaved = guideRepository.save(withdraw);

        UseWithResponse useResponse = UseWithResponse.builder()
                .guideId(useSaved.getId())
                .content(useSaved.getContent())
                .build();

        UseWithResponse withResponse = UseWithResponse.builder()
                .guideId(withSaved.getId())
                .content(withSaved.getContent())
                .build();

        List<UseWithResponse> responses = List.of(useResponse,withResponse);

        return responses;
    }

    @Transactional
    public GuideResponse saveGuide(CustomUserDetails customUserDetails, String type, GuideRequest request){
        globalService.validateAdmin(customUserDetails);

        Guide guide = Guide.builder()
                .title(type)
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .updateBy(customUserDetails.getId())
                .build();

        Guide saved = guideRepository.save(guide);

        return GuideResponse.builder()
                .guideId(saved.getId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .image_url(saved.getImageUrl())
                .updateBy(saved.getUpdateBy())
                .build();
    }

    public GuideResponse getGuide(CustomUserDetails customUserDetails, String type){
        globalService.validateAdmin(customUserDetails);

        Guide guide = guideRepository.findGuideByTitle(type)
                .orElseThrow(() -> new CustomException(ErrorCode.GUIDE_NOT_FOUND));

        return GuideResponse.builder()
                .guideId(guide.getId())
                .title(guide.getTitle())
                .content(guide.getContent())
                .image_url(guide.getImageUrl())
                .updateBy(guide.getUpdateBy())
                .build();
    }

    @Transactional
    public GuideResponse updateGuide(CustomUserDetails customUserDetails, String type, GuideRequest request){
        globalService.validateAdmin(customUserDetails);

        Guide guide = guideRepository.findGuideByTitle(type)
                .orElseThrow(() -> new CustomException(ErrorCode.GUIDE_NOT_FOUND));

        guide.update(request.getImageUrl(),request.getContent());
        Guide saved = guideRepository.save(guide);

        return GuideResponse.builder()
                .guideId(saved.getId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .image_url(saved.getImageUrl())
                .updateBy(saved.getUpdateBy())
                .build();
    }
}
