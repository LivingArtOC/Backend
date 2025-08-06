package livart.common.dto.enums.alarm;

import javax.swing.plaf.PanelUI;
import java.util.Arrays;

public enum SmsType {
    ORDER_RECEIPT, // 주문 접수
    PAID, // 결제 완료
    PENDING, // 입금 대기
    DELIVERY_START, // 상품 배송 시작 안내
    INVOICE_NUM, // 송장 번호 안내
    DELIVERED, // 배송 완료
    ORDER_CANCELED, // 주문 취소
    REFUNDED, // 환불 완료
    SOLD_OUT, // 상품 품절
    SIGNUP, // 회원 가입
    PASSWORD_CERT, // 비밀번호 찾기 인증번호
    DORMANT_TRANS_INST, // 휴면회원 사전 전환 안내
    DORMANT_CERT, // 휴면회원 해제 인증번호
    MILEAGE_GRANT, // 마일리지 지급 안내
    MILEAGE_DEDUCT, // 마일리지 차감 안내
    MILEAGE_EXT, // 마일리지 소멸 안내
    PRODUCT_REVIEW, // 상품 후기
    INQUIRY, // 1:1 문의 등록
    RESTOCK, // 재입고 알림
    QUOTATION_CREATE, // 견적서 생성 알림
    ADMIN_ADDITION // 관리자 추가
}
