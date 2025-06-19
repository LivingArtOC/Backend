package livart.erp.domain.portfolio.dto.request;

import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

import java.util.List;

@Getter
public class PfRegisterRequest {
    private String companyName;
    private String location;
    private String concept;
    private String description;
    private DateSearchDto registerDate;
    private List<PfItemRequest> itemList;
    private List<PfImageRequest> imageList;
}
