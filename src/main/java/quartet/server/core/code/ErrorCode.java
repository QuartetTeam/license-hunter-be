package quartet.server.core.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements CommonCode {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 Http Method 입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "입력 값의 타입이 올바르지 않습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "잘못된 접근입니다."),
    NOT_EXIST_TOKEN_INFO(HttpStatus.FORBIDDEN, "토큰 정보가 존재하지 않습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근이 거부 되었습니다."),
    FEIGN_CLIENT_ERROR(HttpStatus.BAD_REQUEST, "정보를 가져올 수 없습니다."),

    // Authorization
    SOCIAL_ACCESS_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 소셜 로그인 토큰입니다."),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    EMAIL_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일 입니다."),
    EMAIL_OR_PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "이메일 혹은 비밀번호가 일치하지 않습니다.");

    private final HttpStatus status;
    private final String message;

    @Override
    public int getStatusValue() {
        return status.value();
    }
    @Override
    public HttpStatus getHttpStatus() {
        return status;
    }
}