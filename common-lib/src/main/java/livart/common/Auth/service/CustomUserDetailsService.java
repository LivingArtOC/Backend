package livart.common.Auth.service;

import livart.common.domain.user.entity.User;
import livart.common.domain.user.repository.UserRepository;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.Auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        log.info("[로그인 시도] loginId: {}", loginId);

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> {
                    log.warn("[로그인 실패] 존재하지 않는 사용자: {}", loginId);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        log.info("[로그인 성공] loginId: {}, role: {}", user.getLoginId(), user.getRole());
        return new CustomUserDetails(user);
    }
}
