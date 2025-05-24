package livart.erp.domain.product.option;

import jakarta.persistence.Column;
import livart.common.dto.enums.product.StockStatus;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class DetailedOptionRequest {
    private Integer orderIndex;
    private String optionName;
    private String valueName; // 옵션 값
    private String imageUrl; // 옵션 이미지
    private String fileName; // 이미지 파일 명
}
