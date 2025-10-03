package livart.shop.domain.design.notice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeListItemResponse {
    private Long id;
    private String title;
    private Boolean pinned;        // 상단 고정 여부
    private LocalDate registerDate; // 최초 등록일 (createdAt의 날짜부)
}