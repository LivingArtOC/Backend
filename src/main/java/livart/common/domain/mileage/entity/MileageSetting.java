package livart.common.domain.mileage.entity;


import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.mileage.PaymentRestrict;
import livart.common.dto.enums.mileage.SalePriceStandard;
import livart.erp.domain.mileage.dto.request.MileageDefaultDto;
import livart.erp.domain.mileage.dto.request.MileageUsePayDto;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "mileage_setting")
@Builder @Entity @Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MileageSetting extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isExpired; // 유효기간 여부
    private Integer expireDate; // 마일리지 적립일 기준 유효기간
    private Integer alarmDate; // 소멸 알림 기준
    private Boolean kakaoAlarm; // 카카오 알림톡 발송

    @Enumerated(EnumType.STRING)
    private SalePriceStandard salePriceStandard; // 구매 금액 기준
    private Integer usableMileageStandard; // 사용 가능 마일리지 금액
    private BigDecimal minPurchasePrice; // 사용 가능 최소 구매 금액
    private Boolean isLimited; // 사용 가능 마일리지 제한 여부
    private BigDecimal maxMileagePercentage; // 적립 퍼센테이지
    private BigDecimal purchaseMileage; // 상품 구매시 지급 마일리지
    private Integer signupMileage; // 신규 회원가입 시 지급하는 마일리지

    @Enumerated(EnumType.STRING)
    private PaymentRestrict paymentRestrict; // 구매 시 마일리지 사용 여부에 따라 마일리지 지급 여부
    private Long defaultUpdatedBy;
    private Long usePayUpdatedBy;

    public void updateDefault(MileageDefaultDto dto, Long updatedBy){
        this.isExpired = dto.getIsExpired();
        this.expireDate = dto.getExpireDate();
        this.alarmDate = dto.getAlarmDate();
        this.kakaoAlarm = dto.getKakaoAlarm();
        this.salePriceStandard = dto.getSalePriceStandard();
        this.defaultUpdatedBy = updatedBy;
    }

    public void updateUsePay(MileageUsePayDto dto, Long updatedBy){
        this.usableMileageStandard = dto.getUsableMileageStandard();
        this.minPurchasePrice = dto.getMinPurchasePrice();
        this.isLimited = dto.getIsLimited();
        this.maxMileagePercentage = dto.getMaxMileagePercentage();
        this.purchaseMileage = dto.getPurchaseMileage();
        this.signupMileage = dto.getSignupMileage();
        this.paymentRestrict = dto.getPaymentRestrict();
        this.usePayUpdatedBy = updatedBy;
    }
}
