package quartet.server.core.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CategoryErrorCode implements ResponseCode{
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."),
    SUB_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 소카테고리가 없습니다."),
    CATEGORY_SELECTION_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "관심 분야는 최대 3개까지 선택할 수 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
