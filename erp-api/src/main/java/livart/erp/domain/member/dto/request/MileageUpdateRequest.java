package livart.erp.domain.member.dto.request;

import livart.common.dto.enums.user.MileageType;
import lombok.Getter;

import java.util.List;

@Getter
public class MileageUpdateRequest {
    private List<Long> idList;
    private Integer amount;
    private MileageType type;
    private String adminMemo;
}
