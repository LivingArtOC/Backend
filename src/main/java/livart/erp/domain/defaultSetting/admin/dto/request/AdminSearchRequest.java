package livart.erp.domain.defaultSetting.admin.dto.request;

import livart.erp.domain.defaultSetting.admin.enums.AdminSearchKey;
import lombok.Getter;

@Getter
public class AdminSearchRequest {
    private AdminSearchKey key;
    private String keyword;
    private Boolean snsNotiEnabled;
}
