package quartet.server.core.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CalendarErrorCode implements ResponseCode {
    CALENDAR_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 캘린더입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
