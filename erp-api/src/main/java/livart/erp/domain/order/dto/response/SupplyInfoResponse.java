package livart.erp.domain.order.dto.response;

import livart.erp.domain.support.quotation.dto.response.PicListResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SupplyInfoResponse {
    private Long orderId;
    private String bizNum; // 사업자 등록번호
    private String corporationName; // 법인명
    private String phoneNum;
    private String faxNum;
    private String deliveryDate;
    private String address;
    private String detailAddress;
    private String accountNum;
}
