package livart.erp.domain.support.notice.dto.request;

import livart.common.dto.enums.notice.NoticeStatus;
import lombok.Getter;

import java.util.List;

@Getter
public class NoticeUpdateRequest {
    private String title;
    private Boolean isPinned;
    private List<ImageRequest> attachment;
    private String content;
    private NoticeStatus status;
}
