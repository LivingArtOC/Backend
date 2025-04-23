package livart.erp.domain.design.dto.response;

import livart.common.dto.enums.PopupType;
import livart.common.dto.enums.PopupStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupResponse {
    private Long popupId;
    private String title;
    private PopupStatus status;
    private PopupType popupType;
    private Integer topLocationPixel;
    private Integer leftLocationPixel;
    private Boolean isHiddenToday;
    private Integer widthPixel;
    private Integer heightPixel;
    private String pageUrl;
    private String parameter;
    private LocalDateTime exposedStartDate; // 노출 시작 시간
    private LocalDateTime exposedEndDate; // 노출 마감 시간
}
