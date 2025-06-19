package livart.erp.domain.defaultSetting.admin.dto.request;

import livart.erp.domain.defaultSetting.admin.enums.AdminSearchKey;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class AdminSearchRequest {
    private AdminSearchKey key;
    private String keyword;
    private Boolean smsNotiEnabled;
}
