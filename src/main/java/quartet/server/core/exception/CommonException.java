package quartet.server.core.exception;

import lombok.Getter;
import quartet.server.core.code.ErrorCode;

@Getter
public abstract class CommonException extends RuntimeException {

    private final ErrorCode errorCode;

    protected CommonException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}