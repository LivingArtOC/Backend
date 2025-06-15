package livart.shop.client.biz;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.client.biz.BizCheckClient;
import livart.common.client.biz.BizCheckResponse;
import livart.common.dto.response.ApiResponse;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "외부 API")
public class BizCheckController {

    private final BizCheckClient bizCheckClient;

    @PutMapping("/biz/validate")
    @Operation(summary = "사업자 등록상태 조회 API", description = "공공데이터 포털 제공 사업자 등록 상태 조회 API입니다.")
    public ResponseEntity<ApiResponse<BizCheckResponse>> validateBusiness(@RequestBody BusinessStatusRequest businessStatusRequest) {
        BizCheckResponse response = bizCheckClient.isValidBizNumber(businessStatusRequest);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
