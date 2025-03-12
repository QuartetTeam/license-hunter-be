package quartet.server.domain.mail.exception;

import quartet.server.core.code.MailErrorCode;

public class MailingAlreadyExistsException extends MailException {
    public MailingAlreadyExistsException() {
        super(MailErrorCode.MAILING_ALREADY_EXISTS);
    }
}
