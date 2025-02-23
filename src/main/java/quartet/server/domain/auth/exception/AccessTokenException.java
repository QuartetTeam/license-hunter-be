package quartet.server.domain.auth.exception;

import quartet.server.core.code.AuthErrorCode;

public class AccessTokenException extends AuthException {
    public AccessTokenException(AuthErrorCode errorCode) {
        super(errorCode);
    }
}
