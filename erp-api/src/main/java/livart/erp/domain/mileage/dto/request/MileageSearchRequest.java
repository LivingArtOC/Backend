package livart.erp.domain.mileage.dto.request;

import livart.common.dto.enums.user.MileageType;
import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

@Getter
public class MileageSearchRequest {
    private SearchKey key;
    private String keyword;
    private DateSearchDto useGrantDate;
    private MileageType type;
}
