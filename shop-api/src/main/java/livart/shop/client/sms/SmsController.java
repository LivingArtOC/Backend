package livart.shop.client.sms;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import livart.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "SMS 인증 번호 관련 API", description = "")
@RequestMapping("api/shop/client/sms")
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/send")
    @Operation(summary = "휴대폰 인증번호 발송 API, 토큰 x")
    public ResponseEntity<ApiResponse<String>> send(@RequestBody @Valid OtpSendRequest request) {
        smsService.sendOtp(request);
        return ResponseEntity.ok(ApiResponse.ok("인증번호가 발송되었습니다."));
    }

    @PostMapping("/verify")
    @Operation(summary = "휴대폰 인증번호 유효성 검사 API, 토큰 x")
    public ResponseEntity<ApiResponse<String>> verify(@RequestBody @Valid OtpVerifyRequest request,
                                                      HttpSession session) {
        smsService.verifyOtp(request, session);
        return ResponseEntity.ok(ApiResponse.ok("인증이 완료되었습니다."));
    }
}
