package livart.erp.domain.mileage.dto.request;

import livart.common.dto.enums.mileage.PaymentRestrict;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class MileageUsePayDto {
    private Integer usableMileageStandard; // 사용 가능 마일리지 금액
    private BigDecimal minPurchasePrice; // 사용 가능 최소 구매 금액
    private Boolean isLimited; // 사용 가능 마일리지 제한 여부
    private BigDecimal maxMileagePercentage; // 적립 퍼센테이지
    private BigDecimal purchaseMileage; // 상품 구매시 지급 마일리지
    private Integer signupMileage; // 신규 회원가입 시 지급하는 마일리지
    private PaymentRestrict paymentRestrict; // 구매 시 마일리지 사용 여부에 따라 마일리지 지급 여부
}
