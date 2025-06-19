package livart.common.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import livart.common.dto.enums.design.PopupType;
import livart.common.dto.enums.design.PopupStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PopupRegisterRequest {
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

    @Schema(description = "노출 시작 시간", example = "2025-05-17 13:30")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime start;

    @Schema(description = "노출 끝 시간", example = "2025-05-17 13:30")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime end;
}
