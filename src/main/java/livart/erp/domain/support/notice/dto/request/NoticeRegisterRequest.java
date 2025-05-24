package livart.erp.domain.support.notice.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class NoticeRegisterRequest {
    private String title;
    private Boolean isPinned;
    private List<ImageRequest> attachment;
    private String content;
}
