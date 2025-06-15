package livart.erp.domain.alarm.dto.response;

import jakarta.persistence.Access;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsContentResponse {
    private Long logId;
    private String content;
}
