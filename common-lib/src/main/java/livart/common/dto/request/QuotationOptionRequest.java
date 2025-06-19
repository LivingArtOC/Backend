package livart.common.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Getter
public class QuotationOptionRequest {
    private String optionName;
    private String optionValue;
}
