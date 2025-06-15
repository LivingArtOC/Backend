package livart.erp.domain.alarm.dto.request;

import livart.common.dto.enums.alarm.DefaultSmsType;
import lombok.Getter;

@Getter
public class SmsUpdateRequest {
    private DefaultSmsType defaultSmsType;
}
