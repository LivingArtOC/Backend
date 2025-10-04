package livart.erp.domain.product.product.dto.request;

import livart.erp.domain.product.product.dto.IdType;
import lombok.Getter;

import java.util.List;

@Getter
public class IdListRequest {
    private Long id;
    private IdType type;

}
