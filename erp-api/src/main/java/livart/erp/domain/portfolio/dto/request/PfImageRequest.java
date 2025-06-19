package livart.erp.domain.portfolio.dto.request;

import livart.common.dto.enums.portfolio.ImageType;
import lombok.Getter;

@Getter
public class PfImageRequest {
    private ImageType imageType;
    private String fileName;
    private String imageUrl;
    private Integer orderIndex; // null 이면 일반 이미지인 경우, 숫자가 있으면 목록 이미지
    private String detailComment;
}
