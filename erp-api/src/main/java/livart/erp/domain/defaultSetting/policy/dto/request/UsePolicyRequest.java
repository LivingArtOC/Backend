package livart.erp.domain.defaultSetting.policy.dto.request;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UsePolicyRequest {
    private String usePolicyContent;
    private String courseContent;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isExposed;
    private String officerName; //보호 책임자 이름
    private String officerPosition; //보호 책임자 직책
    private String officerPhone; //보호 책임자 전번
    private String officerEmail; //보호 책임자 이메일
}
