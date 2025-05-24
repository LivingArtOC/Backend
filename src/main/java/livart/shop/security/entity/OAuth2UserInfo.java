package livart.shop.security.entity;

import livart.common.domain.user.entity.User;
import livart.common.dto.enums.user.Provider;
import livart.common.dto.enums.user.Role;
import livart.common.dto.enums.user.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@Builder
@ToString
public class OAuth2UserInfo {
    private String socialId;       // 소셜 고유 ID
    private String email;          // 제공되는 경우만
    private String name;
    private Provider provider;

    public static OAuth2UserInfo of(String provider, Map<String, Object> attributes) {
        switch (provider) {
            case "google": return ofGoogle(attributes);
            case "kakao": return ofKakao(attributes);
            case "naver": return ofNaver(attributes);
            default: throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다.");
        }
    }

    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .provider(Provider.GOOGLE)
                .socialId((String) attributes.get("sub"))
                .email((String) attributes.get("email"))
                .name((String) attributes.get("name"))
                .build();
    }

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        return OAuth2UserInfo.builder()
                .provider(Provider.KAKAO)
                .socialId(String.valueOf(attributes.get("id")))
                .email((String) kakaoAccount.get("email")) // null 가능
                .name((String) properties.get("nickname")) // nickname을 name으로 통일
                .build();
    }

    private static OAuth2UserInfo ofNaver(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2UserInfo.builder()
                .provider(Provider.NAVER)
                .socialId((String) response.get("id"))
                .email((String) response.get("email"))
                .name((String) response.get("name")) // 실명
                .build();
    }

    public User toEntity() {
        return User.builder()
                .loginId(null)
                .password(null)
                .email(null)
                .provider(this.provider)
                .socialId(this.socialId)
                .role(Role.CONSUMER)
                .status(UserStatus.ACTIVE)
                .build();
    }
}

