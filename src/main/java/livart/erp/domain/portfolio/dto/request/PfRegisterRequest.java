package livart.erp.domain.portfolio.dto.request;

import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

import java.util.List;

@Getter
public class PfRegisterRequest {
    private Long orderId; // null 이면 거래 정보 없이 등록
    private String companyName;
    private String location;
    private String concept;
    private String description;
    private DateSearchDto registerDate;
    private List<PfItemRequest> itemList;
    private List<PfImageRequest> imageList;
}
