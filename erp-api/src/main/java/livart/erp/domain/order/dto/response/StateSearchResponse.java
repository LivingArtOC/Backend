package livart.erp.domain.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDate;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class StateSearchResponse {
    private Long orderId;
    private String orderNum;
    private String orderName;
    private String orderPhoneNum;
    private LocalDate paidDate;

}
