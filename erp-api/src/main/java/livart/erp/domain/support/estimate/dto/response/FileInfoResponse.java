package livart.erp.domain.support.estimate.dto.response;

import livart.common.dto.enums.estimate.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoResponse {
    private Long fileId;
    private FileType type;
    private String fileName;
    private String fileUrl;
}
