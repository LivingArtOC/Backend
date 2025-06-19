package livart.erp.domain.product.excel;

import livart.erp.domain.product.product.dto.request.IdListRequest;
import lombok.Getter;

import java.util.List;

@Getter
public class ExcelDownloadRequest {
    private List<Long> idList;
    private List<String> fields;
    private String fileName;
    private Boolean usePassword;
    private String password;
}
