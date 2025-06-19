package livart.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsSendResult {
    private Long recipientCount;
    private Long successCount;
    private Long reserveCount;
    private Long failCount;
}
