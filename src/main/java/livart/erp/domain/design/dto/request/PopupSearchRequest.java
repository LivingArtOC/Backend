package livart.erp.domain.design.dto.request;

import livart.common.dto.enums.PopupType;
import livart.common.dto.enums.PopupStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PopupSearchRequest {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private PopupStatus status;
    private PopupType type;
}
