package livart.erp.domain.defaultSetting.policy.dto.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UsePolicyRequest {
    private Boolean isRequired;
    private String content;
    private String course;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isExposed;
}
