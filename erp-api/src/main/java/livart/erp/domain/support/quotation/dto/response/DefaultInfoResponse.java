package livart.erp.domain.support.quotation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class DefaultInfoResponse {
    private String bizNum; // 공급자 정보
    private String corporationName;
    private String presidentName;
    private String address;
    private String detailAddress;
    private List<PicListResponse> picList;
}
