package livart.erp.domain.facility.service;

import livart.common.Auth.CustomUserDetails;
import livart.common.domain.social.entity.SocialAPI;
import livart.common.domain.social.repository.SocialAPIRepository;
import livart.common.domain.user.entity.Admin;
import livart.common.dto.enums.Provider;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.service.GlobalService;
import livart.erp.domain.facility.dto.request.SocialRequest;
import livart.erp.domain.facility.dto.response.SocialResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class SocialAPIService {

    private final GlobalService globalService;
    private final SocialAPIRepository socialAPIRepository;

    @Transactional
    public SocialResponse updateSetting(CustomUserDetails customUserDetails, SocialRequest request){
        globalService.findUser(customUserDetails);

        SocialAPI socialAPI = socialAPIRepository.findByProvider(request.getProvider())
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        socialAPI.update(request.getClientId(), request.getClientSecret(), customUserDetails.getId());
        SocialAPI saved = socialAPIRepository.save(socialAPI);

        return SocialResponse.builder()
                .provider(saved.getProvider())
                .clientId(saved.getClientId())
                .clientSecret(saved.getClientSecret())
                .adminId(saved.getAdminId())
                .updatedAt(saved.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .build();
    }

    public SocialResponse getSetting(CustomUserDetails customUserDetails, String provider){
        globalService.findUser(customUserDetails);

        SocialAPI socialAPI = socialAPIRepository.findByProvider(Provider.valueOf(provider))
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        return SocialResponse.builder()
                .provider(socialAPI.getProvider())
                .clientId(socialAPI.getClientId())
                .clientSecret(socialAPI.getClientSecret())
                .adminId(socialAPI.getAdminId())
                .updatedAt(socialAPI.getUpdatedAt().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime())
                .build();
    }
}
