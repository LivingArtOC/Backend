package livart.erp.domain.order.dto.response;

import livart.common.dto.enums.order.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsChangeStatusResponse {
    private Long asId;
    private RequestStatus status;
    private LocalDateTime changedAt;
}
