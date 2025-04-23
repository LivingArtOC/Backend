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

    // 401 UNAUTHORIZED
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "허가되지 않은 사용자입니다."),
    PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    AUTH_CODE_MISMATCH(HttpStatus.UNAUTHORIZED, "인증번호가 일치하지 않습니다."),
    AUTH_CODE_EXPIRED(HttpStatus.UNAUTHORIZED, "인증번호 유효시간이 지났습니다."),

    // 403 FORBIDDEN
    ADMIN_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 사이트는 관리자만 이용하실 수 있습니다."),
    USER_STATUS_BLOCKED(HttpStatus.FORBIDDEN, "사용자의 계정이 삭제되었거나 존재하지 않습니다."),
    ADMIN_LOGIN_DISABLED(HttpStatus.FORBIDDEN, "해당 사이트에 대한 접근이 제한되어 있습니다."),
    ACCESS_DENIED_BY_IP(HttpStatus.FORBIDDEN, "허용되지 않은 IP로 접근은 불가합니다."),

    // 404 NOT_FOUND
    INVALID_BIZ_NUMBER(HttpStatus.NOT_FOUND, "사업자 상태 조회 실패"),
    BRAND_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "브랜드 소개 정보가 존재하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    GUIDE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 안내 사항를 찾을 수 없습니다."),
    TERM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 약관을 찾을 수 없습니다."),
    POPUP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 팝업을 찾을 수 없습니다."),
    INTERIOR_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "전시장 안내 사항을 찾을 수 없습니다."),
    CATALOG_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카탈로그를 찾을 수 없습니다."),
    COUPON_SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카탈로그를 찾을 수 없습니다."),
    DETAIL_TERM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 세부 약관을 찾을 수 없습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 자원을 찾을 수 없습니다."),
    AUTH_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "인증 코드를 찾을 수 없습니다."),

    // 405 METHOD_NOT_ALLOWED
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 HTTP 메서드입니다."),

    // 409 CONFLICT
    DUPLICATED_LOGIN_ID(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
    DUPLICATE_BIZ_NUM(HttpStatus.CONFLICT, "이미 존재하는 사업자 등록 번호입니다."),

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


