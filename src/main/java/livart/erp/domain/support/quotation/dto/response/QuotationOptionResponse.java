package livart.erp.domain.support.quotation.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuotationOptionResponse {
    private String optionName;
    private String optionValue;
}
