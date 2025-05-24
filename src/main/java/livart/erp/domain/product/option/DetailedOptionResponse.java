package livart.erp.domain.product.option;

import livart.common.dto.enums.product.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailedOptionResponse {
    private Long detailOptionId;
    private String optionName;
    private String valueName; // 옵션 값
    private String imageUrl; // 옵션 이미지
    private String fileName; // 이미지 파일 명
}
