package livart.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 400 BAD_REQUEST
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력 값입니다."),
    NULL_INPUT_VALUE(HttpStatus.BAD_REQUEST, "요청 본문이 비어있습니다"),
    NULL_INPUT_JWT_KEY(HttpStatus.BAD_REQUEST, "JWT secret key 값이 null이나 비어있습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 비밀번호 형식입니다."),
    INVALID_LOGIN_ID_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 아이디 형식입니다."),
    DUPLICATE_ORDER_VALUE(HttpStatus.BAD_REQUEST, "중복된 순서가 존재합니다."),
    INVALID_COUPON_ISSUED_STATUS(HttpStatus.BAD_REQUEST, "잘못된 쿠폰 발급 상태입니다."),
    INVALID_ORDER_ITEM_STATUS(HttpStatus.BAD_REQUEST, "잘못된 주문 상품 상태입니다."),
    INVALID_NOTICE_STATUS(HttpStatus.BAD_REQUEST, "잘못된 공지 상태입니다."),
    INVALID_INQUIRY_STATUS(HttpStatus.BAD_REQUEST, "잘못된 고객 문의 상태입니다."),
    INVALID_EXCEL_FILE(HttpStatus.BAD_REQUEST, "잘못된 엑셀 파일입니다."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "잘못된 ROLE 입니다."),
    INVALID_USER_STATUS(HttpStatus.BAD_REQUEST, "잘못된 유저 상태 입니다."),
    INVALID_SOCIAL_PROVIDER(HttpStatus.BAD_REQUEST, "잘못된 소셜 타입 입니다."),
    INVALID_TYPE(HttpStatus.BAD_REQUEST, "잘못된 타입 입력 입니다."),
    INVALID_REQUEST_TYPE(HttpStatus.BAD_REQUEST, "잘못된 요청 타입 입력입니다."),
    NULL_INPUT_IP_LIST(HttpStatus.BAD_REQUEST, "IP 리스트가 NULL 입니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "이미지 크기는 50MB 이하만 가능합니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "업로드한 이미지 형식이 기준에 적합하지 않습니다."),
    INVALID_PRODUCT_STATUS(HttpStatus.BAD_REQUEST, "삭제 상태의 제품만 완전 삭제하실 수 있습니다."),
    INVALID_KEYWORD_FORMAT(HttpStatus.BAD_REQUEST, "숫자만 입력하실 수 있습니다."),
    INVALID_SMS_SETTING(HttpStatus.BAD_REQUEST, "잘못된 SMS 설정 입력값입니다."),
    INVALID_EMAIL_SETTING(HttpStatus.BAD_REQUEST, "잘못된 EMAIL 설정 입력값입니다."),
    INVALID_SEND_TARGET(HttpStatus.BAD_REQUEST, "잘못된 발송 대상입니다."),
    INVALID_IMAGE_SIZE(HttpStatus.BAD_REQUEST, "잘못된 이미지 개수 입니다."),
    INVALID_SORT_PARAM_VARIABLES(HttpStatus.BAD_REQUEST, "잘못된 정렬 변수가 주소 값으로 들어왔습니다."),
    INVALID_PRODUCT_ID(HttpStatus.BAD_REQUEST, "PRODUCT"),

    // 401 UNAUTHORIZED
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "허가되지 않은 사용자입니다."),
    PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    AUTH_CODE_MISMATCH(HttpStatus.UNAUTHORIZED, "인증번호가 일치하지 않습니다."),
    AUTH_CODE_EXPIRED(HttpStatus.UNAUTHORIZED, "인증번호 유효시간이 지났습니다."),
    PHONE_AUTHORIZED(HttpStatus.UNAUTHORIZED, "휴대폰 인증이 필요합니다."),
    SMS_BALANCE_FETCH_FAILED(HttpStatus.UNAUTHORIZED, "Coolsms 잔여 포인트 조회에 실패했습니다"),

    // 403 FORBIDDEN
    ADMIN_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 사이트는 관리자만 이용하실 수 있습니다."),
    ONLY_SUPER_ADMIN(HttpStatus.FORBIDDEN, "해당 페이지는 최고 관리자만 접근 가능합니다."),
    USER_STATUS_BLOCKED(HttpStatus.FORBIDDEN, "사용자의 계정이 삭제되었거나 존재하지 않습니다."),
    ADMIN_LOGIN_DISABLED(HttpStatus.FORBIDDEN, "해당 사이트에 대한 접근이 제한되어 있습니다."),
    ACCESS_DENIED_BY_IP(HttpStatus.FORBIDDEN, "허용되지 않은 IP로 접근은 불가합니다."),
    LOGIN_RESTRICT(HttpStatus.FORBIDDEN, "비밀번호 5회 실패로 인해 1시간동안 로그인이 제한되셨습니다."),
    TEMPLATE_NOT_APPROVED(HttpStatus.FORBIDDEN, "허용되지 않은 알림톡 템플릿은 사용 불가합니다."),

    // 404 NOT_FOUND
    INVALID_BIZ_NUMBER(HttpStatus.NOT_FOUND, "사업자 상태 조회 실패"),
    BRAND_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "브랜드 소개 정보가 존재하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 Refresh Token을 찾을 수 없습니다."),
    GUIDE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 안내 사항를 찾을 수 없습니다."),
    TERM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 약관을 찾을 수 없습니다."),
    POPUP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 팝업을 찾을 수 없습니다."),
    INTERIOR_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "전시장 안내 사항을 찾을 수 없습니다."),
    CATALOG_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카탈로그를 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리를 찾을 수 없습니다."),
    COUPON_SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠폰 기본 설정 내용을 찾을 수 없습니다."),
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 쿠폰을 찾을 수 없습니다."),
    DETAIL_TERM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 세부 약관을 찾을 수 없습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 자원을 찾을 수 없습니다."),
    AUTH_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "인증 코드를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 제품을 찾을 수 없습니다."),
    PRODUCT_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 제품 옵션을 찾을 수 없습니다."),
    PORTFOLIO_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 납품 사례를 찾을 수 없습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문을 찾을 수 없습니다."),
    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문 상품을 찾을 수 없습니다."),
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 공지를 찾을 수 없습니다."),
    FAQ_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 FAQ를 찾을 수 없습니다."),
    INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 고객 문의를 찾을 수 없습니다."),
    ESTIMATE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 견적 요청을 찾을 수 없습니다."),
    DETAILED_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 세부 옵션을 찾을 수 없습니다."),
    PERSONAL_PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 견적 요청에 대한 개인 결제창을 찾을 수 없습니다."),
    COMPANY_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "회사 기본 정보를 찾을 수 없습니다."),
    QUOTATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 견적서를 찾을 수 없습니다."),
    PROVIDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 소셜의 KEY 정보를 찾을 수 없습니다."),
    OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 옵션 정보를 찾을 수 없습니다."),
    MILEAGE_SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "마일리지 설정 정보를 찾을 수 없습니다."),
    SMS_SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "SMS 관련 설정 정보를 찾을 수 없습니다."),
    EMAIL_SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "EMAIL 관련 설정 정보를 찾을 수 없습니다."),
    EMAIL_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일 발송 기록을 찾을 수 없습니다."),
    SMS_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 SMS 템플릿을 찾을 수 없습니다."),
    SMS_CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "저장된 메시지 중 해당 SMS 메시지를 찾을 수 없습니다."),
    SMS_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 SMS 발송 기록을 찾을 수 없습니다."),
    TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 알림톡 템플릿을 찾을 수 없습니다."),
    CLAIM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 CLAIM 요청을 찾을 수 없습니다."),
    AS_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 AS 요청을 찾을 수 없습니다."),

    // 405 METHOD_NOT_ALLOWED
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 HTTP 메서드입니다."),

    // 409 CONFLICT
    DUPLICATED_LOGIN_ID(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
    DUPLICATE_PORTFOLIO(HttpStatus.CONFLICT, "이미 해당 주문에 대해 납품 사례가 존재합니다."),
    DUPLICATE_BIZ_NUM(HttpStatus.CONFLICT, "이미 존재하는 사업자 등록 번호입니다."),
    PINNED_PRODUCT_CANNOT_HAVE_MANUAL_ORDER(HttpStatus.CONFLICT, "고정된 제품은 순서 변경 불가"),
    PINNED_CANNOT_HAVE_MANUAL_ORDER(HttpStatus.CONFLICT, "고정된 항목은 순서 변경 불가"),

    // 500 INTERNAL_SERVER_ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    SMS_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "인증번호 발송에 실패했습니다.");


    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.httpStatus = status;
        this.message = message;
    }
}


