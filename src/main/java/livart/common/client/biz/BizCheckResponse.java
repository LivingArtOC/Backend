package livart.common.client.biz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class BizCheckResponse {
    private boolean isExist;
    private String bizType; // 업종
    private String bizStatus; // 업태
    private String bizName;
    private String ownerName;
    private String bizNum;
}
