package livart.erp.domain.facility.service;

import livart.common.Auth.CustomUserDetails;
import livart.common.domain.social.entity.SocialAPI;
import livart.common.domain.social.repository.SocialAPIRepository;
import livart.common.dto.enums.user.Provider;
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
        globalService.validateAdmin(customUserDetails);

        String prov = request.getProvider().toUpperCase();

        if (!Provider.containSocial(prov) ||
                prov.equals(Provider.ALL.name()) ||
                prov.equals(Provider.LOCAL.name())) {
            throw new CustomException(ErrorCode.INVALID_SOCIAL_PROVIDER);
        }

        Provider provider = Provider.valueOf(prov.toUpperCase());

        SocialAPI socialAPI = socialAPIRepository.findByProvider(provider)
                .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_NOT_FOUND));

        socialAPI.update(request.getClientId(), request.getClientSecret(), customUserDetails.getId());
        SocialAPI saved = socialAPIRepository.save(socialAPI);

        return SocialResponse.builder()
                .provider(saved.getProvider())
                .clientId(saved.getClientId())
                .clientSecret(saved.getClientSecret())
                .build();
    }

    public SocialResponse getSetting(CustomUserDetails customUserDetails, String pr){
        globalService.validateAdmin(customUserDetails);

        String prov = pr.toUpperCase();

        if (!Provider.containSocial(prov) ||
                prov.equals(Provider.ALL.name()) ||
                prov.equals(Provider.LOCAL.name())) {
            throw new CustomException(ErrorCode.INVALID_SOCIAL_PROVIDER);
        }

        Provider provider = Provider.valueOf(prov.toUpperCase());

        SocialAPI socialAPI = socialAPIRepository.findByProvider(provider)
                .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_NOT_FOUND));

        return SocialResponse.builder()
                .provider(socialAPI.getProvider())
                .clientId(socialAPI.getClientId())
                .clientSecret(socialAPI.getClientSecret())
                .build();
    }
}
