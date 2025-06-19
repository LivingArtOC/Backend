package livart.erp.domain.alarm.dto.request;

import livart.common.dto.enums.alarm.EmailAutoType;
import livart.common.dto.enums.alarm.EmailType;
import lombok.Data;

@Data
public class EmailAutoRequest {
    private EmailAutoType emailAutoType;
    private Boolean isAutoSend;
    private Integer sendStandardDate;
    private Integer sendMethod;
    private String title;
    private String content;
}
