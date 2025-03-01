package quartet.server.domain.certification.exception;

import quartet.server.core.code.ResponseCode;
import quartet.server.core.exception.BaseException;

public class CertificationException extends BaseException {
    protected CertificationException (ResponseCode errorCode){super(errorCode);}
}
