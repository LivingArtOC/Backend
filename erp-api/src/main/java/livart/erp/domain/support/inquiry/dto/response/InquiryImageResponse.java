package livart.erp.domain.support.inquiry.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InquiryImageResponse {
    private Long imageId;
    private String fileName;
    private String imgUrl;
}
