package livart.shop.domain.design.notice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeViewResponse {
    private Long id;
    private String title;
    private String content;                 // ERP 저장 그대로(텍스트/HTML)
    private List<NoticeImageResponse> images;
    private LocalDate registerDate;         // 최초 등록일
}