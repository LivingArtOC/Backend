package livart.erp.domain.defaultSetting.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AdminSearchResponse {
    private Long adminId;
    private String loginId;
    private String adminName;
    private String department;
    private String position;
    private String roleTitle;
    private String phoneNum;
    private String officeNum;
    private LocalDate registerdAt;
    private LocalDateTime lastLogin;
}
