package livart.common.dto.enums.alarm;

import java.util.Arrays;

public enum EmailType {
    CHECK_ORDER_LOG, // 주문 내역 확인
    CHECK_PAYMENT, // 입금 확인
    PRODUCT_DELIVERY, // 상품 배송
    SIGNUP, // 회원가입
    DORMANT_INST, // 휴면회원 사전 안내
    DORMANT_REL_CERT, // 휴면회원 해제 인증
    DORMANT_REL_INST, // 휴면회원 해제 안내
    PASSWORD_CHANGE, // 비밀번호 변경 알림
    POST_ANSWER, // 게시글 답변
    WITHDRAW, // 회원 탈퇴
    MILEAGE_GRANT, // 마일리지 지급
    MILEAGE_DEDUCT, // 마일리지 차감
    MILEAGE_EXT; // 마일리지 소멸 예정


    public static boolean contains(String value){
        return Arrays.stream(EmailType.values())
                .anyMatch(t -> t.name().equals(value.toUpperCase()));
    }
}
