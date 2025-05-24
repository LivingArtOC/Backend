package livart.common.Auth;

import livart.common.domain.user.entity.User;
import livart.common.dto.enums.user.Provider;
import livart.common.dto.enums.user.Role;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Slf4j
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;

    public CustomUserDetails(User user) {
        this.user = user;
        this.attributes = Collections.emptyMap();
    }

    public CustomUserDetails(User user,
                             Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    public Long getId() {
        return user.getId();
    }

    public Role getRole() {
        return user.getRole();
    }

    public Provider getProvider() { return user.getProvider(); }

    public String getSocialId() {
        return user.getSocialId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        log.info("✅ getPassword()에서 반환하는 값: {}", user.getPassword());
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getLoginId();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return user.getEmail(); // 또는 user.getLoginId()도 가능
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 유효기간 관리
    }

    @Override
    public boolean isEnabled() { return true; } //계정 활성화 여부
}

