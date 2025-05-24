package livart.erp.domain.design.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import livart.common.dto.enums.design.PopupType;
import livart.common.dto.enums.design.PopupStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private LocalDate createdAt;
    private LocalDate updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime exposedStartDate; // 노출 시작 시간

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime exposedEndDate; // 노출 마감 시간
}
