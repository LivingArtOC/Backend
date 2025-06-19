package livart.common.domain.social.entity;

import livart.common.domain.social.repository.SocialAPIRepository;
import livart.common.dto.enums.user.Provider;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class DynamicClientRegistrationRepository implements ClientRegistrationRepository, Iterable<ClientRegistration> {

    private final SocialAPIRepository socialAPIRepository;
    private final Map<String, ClientRegistration> registrations = new ConcurrentHashMap<>();
    private final Map<String, ProviderMetadata> providerMetadataMap = Map.of(
            "kakao", new ProviderMetadata(
                    "https://kauth.kakao.com/oauth/authorize",
                    "https://kauth.kakao.com/oauth/token",
                    "https://kapi.kakao.com/v2/user/me",
                    "id"
            ),
            "naver", new ProviderMetadata(
                    "https://nid.naver.com/oauth2.0/authorize",
                    "https://nid.naver.com/oauth2.0/token",
                    "https://openapi.naver.com/v1/nid/me",
                    "response"
            ),
            "google", new ProviderMetadata(
                    "https://accounts.google.com/o/oauth2/v2/auth",
                    "https://oauth2.googleapis.com/token",
                    "https://www.googleapis.com/oauth2/v3/userinfo",
                    "sub"
            )
    );

    @Override
    public ClientRegistration findByRegistrationId(String registrationId) {
        SocialAPI socialAPI = socialAPIRepository.findByProvider(Provider.valueOf(registrationId.toUpperCase()))
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        ProviderMetadata metadata = providerMetadataMap.get(registrationId.toLowerCase());
        return ClientRegistration.withRegistrationId(registrationId)
                .clientId(socialAPI.getClientId())
                .clientSecret(socialAPI.getClientSecret())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/" + registrationId)
                .authorizationUri(metadata.authorizationUri())
                .tokenUri(metadata.tokenUri())
                .userInfoUri(metadata.userInfoUri())
                .userNameAttributeName(metadata.userNameAttributeName())
                .scope(getScopeByProvider(registrationId))
                .clientName(registrationId)
                .build();
    }

    private Set<String> getScopeByProvider(String provider) {
        return switch (provider.toLowerCase()) {
            case "google" -> Set.of("profile", "email");
            case "naver" -> Set.of("name", "email");
            case "kakao" -> Set.of("profile_nickname");
            default -> Set.of();
        };
    }

    @Override
    public Iterator<ClientRegistration> iterator() {
        return socialAPIRepository.findAll().stream()
                .map(api -> findByRegistrationId(api.getProvider().name().toLowerCase()))
                .iterator();
    }

    private record ProviderMetadata(String authorizationUri, String tokenUri, String userInfoUri, String userNameAttributeName) {}
}
