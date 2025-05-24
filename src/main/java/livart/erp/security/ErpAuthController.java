package livart.erp.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import livart.common.Auth.AuthTokens;
import livart.common.Auth.CustomUserDetails;
import livart.common.Auth.dto.request.LoginRequest;
import livart.common.Auth.dto.request.RefreshTokenRequest;
import livart.common.Auth.repository.RefreshTokenRepository;
import livart.common.Auth.util.JwtTokenProvider;
import livart.common.domain.user.repository.UserRepository;
import livart.common.dto.response.ApiResponse;
import livart.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/erp/auth")
@RequiredArgsConstructor
@Tag(name = "로그인/회원가입 - 로그인 관련 API", description = "✅ 개발 완료")
public class ErpAuthController {
    private final ErpAuthService erpAuthService;
    @PostMapping("/login/test")
    @Operation(summary = "✅ 테스트 로그인 API, 토큰 X")
    public ResponseEntity<ApiResponse<AuthTokens>> login(@RequestBody LoginRequest request, HttpServletRequest httpServletRequest) throws IOException {
        return ResponseEntity.ok(ApiResponse.ok(erpAuthService.login(request, httpServletRequest)));
    }

    @PostMapping("/logout")
    @Operation(summary = "✅ 로그아웃 API")
    public ResponseEntity<ApiResponse<String>> logout(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        erpAuthService.logout(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok("로그아웃 완료"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "✅ 새로운 Access Token 발급 API")
    public ResponseEntity<ApiResponse<AuthTokens>> refresh(HttpServletRequest request) {
        String refreshToken = erpAuthService.extractFromAuthorizationHeader(request);
        AuthTokens tokens = erpAuthService.refresh(refreshToken);
        return ResponseEntity.ok(ApiResponse.ok(tokens));
    }
}

