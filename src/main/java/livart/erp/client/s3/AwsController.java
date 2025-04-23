package livart.erp.client.s3;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.client.s3.S3Uploader;
import livart.common.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Tag(name = "AWS 관련 테스트 API")
@RequestMapping("api/erp/client")
public class AwsController {

    private final S3Uploader s3Uploader;

    @PostMapping("/single-image")
    @Operation(summary = "S3 단일 이미지 저장 API")
    public ResponseEntity<ApiResponse<UploadResult>> uploadCompanySeal(@RequestPart MultipartFile file,
                                                                 @RequestParam String dir) throws IOException {
        UploadResult url = s3Uploader.upload(file, dir);
        return ResponseEntity.ok(ApiResponse.ok(url));
    }
}
