package livart.shop.security.service;

import livart.common.Auth.CustomUserDetails;
import livart.common.domain.user.entity.User;
import livart.common.domain.user.repository.UserRepository;
import livart.shop.security.entity.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();
        log.info("provider : {}", provider);

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(provider, oAuth2User.getAttributes());
        log.info("oAuth2UserInfo : {}", oAuth2UserInfo.toString());

        User user = userRepository.findBySocialIdAndProvider(oAuth2UserInfo.getSocialId(), oAuth2UserInfo.getProvider())
                .orElseGet(() -> userRepository.save(oAuth2UserInfo.toEntity()));

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }
}

