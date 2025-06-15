package livart.erp.domain.alarm.dto.response;

import livart.common.dto.enums.alarm.EmailForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.ses.endpoints.internal.Value;

import java.time.LocalDate;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailSearchResponse {
    private Long logId;
    private EmailForm emailForm;
    private String title;
    private String senderName;
    private LocalDate sendDate;
    private String recipientEmail;
}
