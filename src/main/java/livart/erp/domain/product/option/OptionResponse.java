package livart.erp.domain.product.option;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptionResponse {
    private Long detailedOptionId;
    private String optionName;
    private String valueName; // 옵션 값
}
