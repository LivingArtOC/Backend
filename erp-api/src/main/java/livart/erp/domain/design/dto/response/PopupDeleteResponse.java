package livart.erp.domain.design.dto.response;

import livart.common.dto.enums.design.PopupStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupDeleteResponse {
    private Long popId;
    private String title;
    private PopupStatus status;
}
