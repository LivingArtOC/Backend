package livart.erp.domain.support.notice.dto.request;

import livart.common.dto.enums.notice.NoticeStatus;
import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

@Getter
public class NoticeSearchRequest {
    private SearchKey key;
    private String keyword;
    private Boolean isPinned;
    private NoticeStatus status;
    private DateSearchDto registerDate;
}
