package livart.erp.domain.defaultSetting.admin.dto.response;

import livart.common.dto.enums.ActionType;
import livart.common.dto.enums.LogType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnifiedLogResponse {
    private LogType logType;
    private String adminLoginId;
    private ActionType actionType;
    private String page;
    private String targetLoginId;
    private String ipAddress;
    private LocalDateTime accessAt;
}
