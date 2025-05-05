package quartet.server.domain.mail.exception;

import quartet.server.core.code.MailErrorCode;

public class MailingNotFoundException extends MailException {
    public MailingNotFoundException() {
        super(MailErrorCode.MAILING_NOT_FOUND);
    }
}