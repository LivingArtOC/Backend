package livart.common.client.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import livart.common.dto.response.UploadResult;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public UploadResult upload(MultipartFile file, String dirName) throws IOException {
        String originalFilename = file.getOriginalFilename().replaceAll(" ", "_");
        String uuid = UUID.randomUUID().toString();
        String fileName = dirName + "/" + uuid + "_" + originalFilename;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);
        String url = amazonS3.getUrl(bucket, fileName).toString();

        return new UploadResult(url, originalFilename);
    }

    public void validateImage(MultipartFile file) {
        // 50MB 제한
        long maxSize = 50 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new CustomException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        // jpg/jpeg 여부 확인
        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.equalsIgnoreCase("image/jpeg") || contentType.equalsIgnoreCase("image/jpg"))) {
            throw new CustomException(ErrorCode.INVALID_FILE_TYPE);
        }
    }
}
