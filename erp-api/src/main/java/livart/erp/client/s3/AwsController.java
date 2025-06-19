package livart.erp.client.s3;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.client.s3.S3Uploader;
import livart.common.dto.response.ApiResponse;
import livart.common.dto.response.UploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Tag(name = "S3 단일 이미지 저장 API", description = "✅✅ 개발 완료")
@RequestMapping("api/erp/client")
public class AwsController {

    private final S3Uploader s3Uploader;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "✅ 일반 이미지 저장 API, 이미지 크기 제한이 따로 없는 경우")
    public ResponseEntity<ApiResponse<UploadResult>> uploadS3Image(
            @Parameter(name = "file", description = "업로드할 이미지 파일", required = true)
            @RequestPart MultipartFile file,
            @RequestParam String dir
    ) throws IOException {
        UploadResult url = s3Uploader.upload(file, dir);
        return ResponseEntity.ok(ApiResponse.ok(url));
    }

    @PostMapping(value = "/brand-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "✅ 브랜드 소개 이미지 저장 API (JPG or JPEG & 50MB 이하 제한)")
    public ResponseEntity<ApiResponse<UploadResult>> uploadBrandImage(
            @Parameter(name = "file", description = "업로드할 이미지 파일", required = true)
            @RequestPart MultipartFile file,
            @RequestParam String dir
    ) throws IOException {
        s3Uploader.validateImage(file);
        UploadResult url = s3Uploader.upload(file, dir);
        return ResponseEntity.ok(ApiResponse.ok(url));
    }

}
