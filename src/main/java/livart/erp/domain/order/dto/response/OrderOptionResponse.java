package livart.erp.domain.order.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class OrderOptionResponse {
    private String optionName;
    private String optionValue;
}
