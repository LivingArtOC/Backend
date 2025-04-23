package livart.shop.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import livart.common.Auth.CustomUserDetails;
import livart.common.dto.response.ApiResponse;
import livart.shop.security.dto.request.BusinessSignupRequest;
import livart.shop.security.dto.request.ConsumerSignupRequest;
import livart.shop.security.dto.request.SocialSignupRequest;
import livart.shop.security.dto.response.SignupResponse;
import livart.shop.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "로그인/회원가입 관련 API")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/")
    @Operation(summary = "배포 테스트용 API", description = "테스트용 입니다.")
    public String home() {
        return "Welcome to the homepage!";
    }

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

}
