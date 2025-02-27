package quartet.server.core.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ResponseCode {
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "인증되지 않은 접근입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),
    EMAIL_OR_PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "이메일 혹은 비밀번호가 일치하지 않습니다."),
    SOCIAL_ACCESS_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 소셜 로그인 엑세스 토큰입니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다."),
    ACCESS_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 액세스 토큰입니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "액세스 토큰이 만료되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}