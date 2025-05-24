package livart.erp.domain.portfolio.dto.response;

import livart.common.dto.request.DateSearchDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class PfResponse {
    private Long portfolioId;
    private Long orderId; // null 이면 거래 정보 없이 등록
    private String companyName;
    private String location;
    private String concept;
    private String description;
    private DateSearchDto registerDate;
    private List<PfItemResponse> itemList;
    private List<PfImageResponse> imageList;
}
