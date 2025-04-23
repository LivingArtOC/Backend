package livart.erp.domain.defaultSetting.policy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UsePolicyResponse {
    private String title;
    private String content;
    private String course;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isExposed;
}
