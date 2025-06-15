package livart.erp.domain.member.dto.request;

import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

@Getter
public class DeleteSearchRequest {
    private DormantDeleteSearchKey key;
    private String keyword;
    private DateSearchDto deleteDate;
    private Boolean deleteByAdmin;
    private Boolean recoverable;
}
