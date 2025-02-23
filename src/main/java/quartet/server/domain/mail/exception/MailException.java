package quartet.server.domain.mail.exception;

import quartet.server.core.code.ResponseCode;
import quartet.server.core.exception.BaseException;

public class MailException extends BaseException {
    public MailException(final ResponseCode errorCode) {
        super(errorCode);
    }
}