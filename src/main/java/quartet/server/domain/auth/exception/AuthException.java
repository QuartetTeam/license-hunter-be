package quartet.server.domain.auth.exception;

import quartet.server.core.code.AuthErrorCode;
import quartet.server.core.exception.BaseException;

public class AuthException extends BaseException {
    protected AuthException(AuthErrorCode errorCode) {
        super(errorCode);
    }
}
