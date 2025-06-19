package livart.common.dto.request;

import livart.common.dto.enums.mileage.SalePriceStandard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class MileageDefaultDto {
    private Boolean isExpired; // 유효기간 여부
    private Integer expireDate; // 마일리지 적립일 기준 유효기간
    private Integer alarmDate; // 소멸 알림 기준
    private Boolean kakaoAlarm; // 카카오 알림톡 발송
    private SalePriceStandard salePriceStandard; // 구매 금액 기준
}
