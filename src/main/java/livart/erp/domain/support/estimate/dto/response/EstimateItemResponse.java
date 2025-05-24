package livart.erp.domain.support.estimate.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EstimateItemResponse {
    private Long itemId;
    private String productName;
    private String brand;
    private Integer quantity;
    private BigDecimal originalPrice; // 이 당시 정가
    private BigDecimal salePrice; // 판매가
    private BigDecimal totalPrice;
    private List<EstimateItemOptionResponse> optionList;
}
