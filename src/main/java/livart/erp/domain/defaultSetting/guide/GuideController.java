package livart.erp.domain.defaultSetting.guide;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.Auth.CustomUserDetails;
import livart.common.dto.response.ApiResponse;
import livart.erp.domain.defaultSetting.guide.dto.request.GuideRequest;
import livart.erp.domain.defaultSetting.guide.dto.response.GuideResponse;
import livart.erp.domain.defaultSetting.guide.dto.request.UseWithRequest;
import livart.erp.domain.defaultSetting.guide.dto.response.UseWithResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "기본 설정 - 안내 문구 설정 관련 API", description = "✅ 개발 완료")
@RequestMapping("api/erp/setting/guide")
public class GuideController {

    private final GuideService guideService;

    @GetMapping("/use-withdraw")
    @Operation(summary = "✅ 이용/탈퇴 안내 문구 조회 API")
    public ResponseEntity<ApiResponse<List<UseWithResponse>>> getUseWith(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<UseWithResponse> response = guideService.getUseWith(customUserDetails);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/use-withdraw")
    @Operation(summary = "✅ 이용/탈퇴 안내 문구 저장 & 수정 API")
    public ResponseEntity<ApiResponse<List<UseWithResponse>>> updateUseWith(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                     @RequestBody UseWithRequest request) {
        List<UseWithResponse> response = guideService.updateUseWith(customUserDetails, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{type}")
    @Operation(summary = "✅ AS/교환/환불/배송 안내 문구 조회 API")
    public ResponseEntity<ApiResponse<GuideResponse>> getGuide(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                               @PathVariable String type) {
        GuideResponse response = guideService.getGuide(customUserDetails,type);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{type}")
    @Operation(summary = "✅ AS/교환/환불/배송 안내 문구 저장 & 수정 API")
    public ResponseEntity<ApiResponse<GuideResponse>> updateGuide(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                  @PathVariable String type,
                                                                 @RequestBody GuideRequest request) {
        GuideResponse response = guideService.updateGuide(customUserDetails, type, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
