package livart.erp.client.s3;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UploadResult {
    private String url;
    private String originalFilename;
}