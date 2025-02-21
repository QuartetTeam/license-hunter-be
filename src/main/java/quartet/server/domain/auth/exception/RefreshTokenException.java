package quartet.server.domain.auth.exception;

import quartet.server.core.code.ResponseCode;
import quartet.server.core.exception.BaseException;

public class RefreshTokenException extends BaseException {
    protected RefreshTokenException(ResponseCode errorCode) {
        super(errorCode);
    }
}
