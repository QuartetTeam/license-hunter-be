package quartet.server.core.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MailErrorCode implements ResponseCode {
    MAILING_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 메일 구독입니다."),
    MAILING_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 구독 중인 자격증입니다."),
    CERTIFICATION_SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND,"자격증의 일정 정보를 찾을 수 없습니다."),
    EMAIL_SENDING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
