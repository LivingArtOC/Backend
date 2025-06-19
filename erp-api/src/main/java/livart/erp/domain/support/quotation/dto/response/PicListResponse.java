package livart.erp.domain.support.quotation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class PicListResponse {
    private Long picId;
    private String picName;
    private String picPhoneNum;
}
