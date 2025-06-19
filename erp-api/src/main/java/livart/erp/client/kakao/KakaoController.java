package livart.erp.client.kakao;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/erp/kakao")
@Tag(name = "카카오 템플릿 설정 관련 API", description = "✅ 연동 X")
public class KakaoController {

    private final KakaoTemplateService kakaoTemplateService;

    @PostMapping("")
    @Operation(summary = "카카오톡 템플릿 신청 API, 일단 현 시점에선 연동 X")
    public ResponseEntity<ApiResponse<String>> register(
            @RequestBody KakaoTemplateRegisterRequest request) {
        kakaoTemplateService.registerTemplate(request);
        return ResponseEntity.ok(ApiResponse.ok("알림톡 템플릿 등록 요청 완료"));
    }
}
