package livart.erp.domain.defaultSetting.admin.dto.request;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class AdminLogSearchRequest {
    private String adminLoginId;
    private LocalDate startDate;
    private LocalDate endDate;

}
