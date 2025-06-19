package livart.erp.domain.member.dto.request;

import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

@Getter
public class DormantSearchRequest {
    private DormantDeleteSearchKey key;
    private String keyword;
    private DateSearchDto transitionDate;
}
