package livart.erp.domain.alarm.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlarmPointResponse {
    @Column(precision = 10, scale = 2)
    private BigDecimal point;
    private LocalDateTime pointUpdateTime;
}
