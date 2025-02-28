package quartet.server.domain.certification.exception;

import quartet.server.core.code.CertificationErrorCode;

public class CertificationNotFoundException extends CertificationException{
    public CertificationNotFoundException(){super(CertificationErrorCode.CERTIFICATION_NOT_FOUND);}
}
