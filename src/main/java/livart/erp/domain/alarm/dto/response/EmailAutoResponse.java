package livart.erp.domain.alarm.dto.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import livart.common.dto.enums.alarm.EmailAutoType;
import livart.common.dto.enums.alarm.EmailType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailAutoResponse {
    private EmailAutoType emailAutoType;
    private EmailType type;
    private Boolean isAutoSend;
    private Integer sendStandardDate;
    private Integer sendMethod;
    private String title;
    private String sendEmail;
    private String content;
}
