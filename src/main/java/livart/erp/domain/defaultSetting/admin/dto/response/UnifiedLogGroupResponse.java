package livart.erp.domain.defaultSetting.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnifiedLogGroupResponse {
    private List<UnifiedLogResponse> memberLogs;
    private List<UnifiedLogResponse> adminLogs;
    private List<UnifiedLogResponse> loginLogs;
}
