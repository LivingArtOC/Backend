package livart.shop.security.entity;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import livart.common.log.entity.LoginHistory;
import livart.common.log.repository.LoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {

    private final LoginHistoryRepository loginHistoryRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String loginId = request.getParameter("loginId"); // 없을 수도 있음
        if (loginId == null){
            loginId = "(social)";
        }

        loginHistoryRepository.save(LoginHistory.builder()
                .loginId(loginId)
                .userId(null)
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .success(false)
                .failReason(exception.getMessage())
                .site("SHOP") // 소셜 로그인은 SHOP에서
                .createdAt(LocalDateTime.now())
                .build());

    }
}

