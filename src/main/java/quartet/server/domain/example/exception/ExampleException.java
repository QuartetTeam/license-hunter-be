package quartet.server.domain.example.exception;

import quartet.server.core.code.ResponseCode;
import quartet.server.core.exception.BaseException;

public class ExampleException extends BaseException {
    public ExampleException(final ResponseCode errorCode) {
        super(errorCode);
    }
}