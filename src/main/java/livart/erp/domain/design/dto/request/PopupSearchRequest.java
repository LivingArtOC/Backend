package livart.erp.domain.design.dto.request;

import livart.common.dto.enums.design.PopupType;
import livart.common.dto.enums.design.PopupStatus;
import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PopupSearchRequest {
    private SearchKey key;
    private String keyword;
    private DateSearchDto registerDate;
    private PopupStatus status;
    private PopupType type;
}
