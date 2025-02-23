package quartet.server.domain.auth.exception;

import quartet.server.core.code.AuthErrorCode;

public class RefreshTokenException extends AuthException {
    public RefreshTokenException(AuthErrorCode errorCode) {
        super(errorCode);
    }
}
