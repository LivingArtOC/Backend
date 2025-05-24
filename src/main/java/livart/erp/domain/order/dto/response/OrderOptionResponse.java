package livart.erp.domain.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderOptionResponse {
    private String optionName;
    private String optionValue;

}
