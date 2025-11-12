package livart.shop.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import livart.common.Auth.AuthTokens;
import livart.common.Auth.CustomUserDetails;
import livart.common.Auth.dto.request.LoginRequest;
import livart.common.dto.response.ApiResponse;
import livart.shop.security.dto.request.*;
import livart.shop.security.dto.response.LoginIdResponse;
import livart.shop.security.dto.response.LoginResponse;
import livart.shop.security.dto.response.SignupResponse;
import livart.shop.security.dto.response.SignupTermsResponse;
import livart.shop.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/shop/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "로그인/회원가입 관련 API")
public class AuthController {

    private final AuthService authService;
    
    @PostMapping("/signup/consumer")
    @Operation(summary = "일반 회원가입 API", description = "토큰 X")
    public ResponseEntity<ApiResponse<SignupResponse>> consumerSignup(@RequestBody ConsumerSignupRequest consumerSignupRequest){
        SignupResponse user = authService.signupConsumer(consumerSignupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(user));
    }

    @PostMapping("/signup/business")
    @Operation(summary = "사업자 회원가입 API", description = "토큰 X")
    public ResponseEntity<ApiResponse<SignupResponse>> businessSignup(@RequestBody BusinessSignupRequest request){
        SignupResponse response = authService.signupBiz(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PostMapping("/signup/social")
    @Operation(summary = "소셜 회원가입 이후 추가 정보 입력 API", description = "토큰 X")
    public ResponseEntity<ApiResponse<SignupResponse>> socialSignup(@RequestBody SocialSignupRequest request,
                                                                    @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                    HttpServletRequest servletRequest){
        SignupResponse response = authService.signupSocial(request,customUserDetails,servletRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PostMapping("/find/id")
    @Operation(summary = "아이디 찾기 API", description = "토큰 X")
    public ResponseEntity<ApiResponse<LoginIdResponse>> findLoginId(@RequestBody LoginIdFindRequest request){
        LoginIdResponse response = authService.findLoginId(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/find/password")
    @Operation(summary = "비밀번호 찾기 API", description = "토큰 X")
    public ResponseEntity<ApiResponse<String>> findPassword(@RequestBody PasswordFindRequest request){
        authService.findPassword(request);
        return ResponseEntity.ok(ApiResponse.ok("인증되었습니다."));
    }

    @PutMapping("/change/password")
    @Operation(summary = "비밀번호 변경 API", description = "토큰 X")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestBody PasswordChangeRequest request){
        authService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.ok("비밀번호가 성공적으로 변경되었습니다."));
    }

    @GetMapping("/terms")
    @Operation(summary = "회원가입 전용 약관 리스트 조회 API", description = "토큰 X")
    public ResponseEntity<ApiResponse<List<SignupTermsResponse>>> getSignupTerms(){
        List<SignupTermsResponse> responses = authService.getSignupTerms();
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @PostMapping("/login/test")
    @Operation(summary = "✅ 테스트 로그인 API, 토큰 X")
    public ResponseEntity<ApiResponse<AuthTokens>> login(@RequestBody LoginRequest request, HttpServletRequest httpServletRequest) throws IOException {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(request, httpServletRequest)));
    }


}
