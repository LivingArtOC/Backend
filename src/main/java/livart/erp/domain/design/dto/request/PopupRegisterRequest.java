package livart.erp.domain.design.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import livart.common.dto.enums.PopupType;
import livart.common.dto.enums.PopupStatus;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;
}
