package livart.erp.domain.order.dto.response;

import livart.common.dto.enums.order.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.ses.endpoints.internal.Value;

import java.time.LocalDate;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsSearchResponse {
    private Long AsId;
    private LocalDate requestDate;
    private LocalDate completeDate;
    private String orderNum;
    private LocalDate orderDate;
    private String orderName;
    private String orderItemName;
    private String reason;
    private String fileUrl;
    private String fileName;
    private RequestStatus status;
}
