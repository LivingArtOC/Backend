package livart.erp.domain.support.notice.dto.response;

import livart.common.dto.enums.notice.NoticeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeSearchResponse {
    private Long noticeId;
    private String title;
    private String author;
    private Boolean isPinned;
    private NoticeStatus status;
    private LocalDate registerDate;
}
