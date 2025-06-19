package livart.erp.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class BizInfoResponse {
    private String bizName;
    private String presidentName;
    private String bizRegisterationNum;
    private String bizStatus;
    private String bizType;
    private String faxNum;
    private String managerName;
    private String managerPhoneNum;
}
