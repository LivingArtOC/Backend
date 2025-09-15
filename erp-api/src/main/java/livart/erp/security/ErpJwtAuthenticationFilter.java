package livart.erp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import livart.common.Auth.CustomUserDetails;
import livart.common.Auth.util.JwtTokenProvider;
import livart.common.domain.user.entity.User;
import livart.common.domain.user.repository.UserRepository;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.erp.security.util.CookieProps;
import livart.erp.security.util.CookieUtil;
import livart.erp.security.util.CsrfTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
public class ErpJwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final CookieProps cookieProps;
    private static final Set<String> SAFE = Set.of("GET","HEAD","OPTIONS","TRACE");


    // CSRF 검사에서 제외할 경로(로그인/리프레시 등)
    private static boolean csrfExcluded(String uri) {
        return uri.startsWith("/api/erp/auth/login")
                || uri.startsWith("/api/erp/auth/refresh")
                || uri.equals("/swagger-ui/index.html")
                || uri.startsWith("/swagger-ui/")
                || uri.startsWith("/v3/api-docs/")
                || uri.equals("/error");
    }

    private static String cookie(HttpServletRequest req, String name) {
        var cs = req.getCookies();
        if (cs == null) return null;
        for (var c : cs) if (name.equals(c.getName())) return c.getValue();
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        final String method = request.getMethod();
        final String uri = request.getRequestURI();

        // 0) CORS preflight는 바로 통과
        if ("OPTIONS".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            boolean isAuthPath = csrfExcluded(uri);

            // 1) access_token 인증 컨텍스트 세팅(단, authPath에서는 검증 스킵)
            String token = cookie(request, "access_token");
            if (token != null && !isAuthPath) {
                if (!jwtTokenProvider.validateToken(token)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                    return;
                }
                String loginId = jwtTokenProvider.extractSubject(token);
                User user = userRepository.findByLoginId(loginId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
                var userDetails = new CustomUserDetails(user);
                var auth = new UsernamePasswordAuthenticationToken(
                        userDetails, token, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            // 2) 변이 요청이면 더블 서브밋 검사 (로그인/리프레시는 제외)
            boolean unsafe = !SAFE.contains(method);
            if (unsafe && !isAuthPath) {
                String csrfCookie = cookie(request, "XSRF-TOKEN");
                String csrfHeader = request.getHeader("X-XSRF-TOKEN");
                if (csrfCookie == null || !csrfCookie.equals(csrfHeader)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF token mismatch");
                    return;
                }
            }

            // 3) 이중 JWT 재검증 생략(익명 + permitAll 경로 허용)

            // 4) (선택) 안전 메서드에서 XSRF-TOKEN 자동 발급
            if (SAFE.contains(method) && cookie(request, "XSRF-TOKEN") == null) {
                CookieUtil.add(response, CookieUtil.build(
                        "XSRF-TOKEN", CsrfTokenUtil.generate(),
                        cookieProps.domain(), "/",
                        false, cookieProps.sameSite(), cookieProps.secure(),
                        Duration.ofMinutes(30)
                ));
            }

        } catch (Exception e) {
            log.error("JWT/XSRF 필터 에러: {}", e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증 실패");
            return;
        }

        chain.doFilter(request, response);
    }
}

