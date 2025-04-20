package quartet.server.core.exception;

import lombok.Getter;
import quartet.server.core.code.ResponseCode;

@Getter
public abstract class BaseException extends RuntimeException {

    private final ResponseCode errorCode;

    protected BaseException(final ResponseCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    protected BaseException(final ResponseCode errorCode, final Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}