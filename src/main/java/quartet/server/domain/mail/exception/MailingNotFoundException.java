package quartet.server.domain.mail.exception;

import quartet.server.core.code.MailErrorCode;
import quartet.server.domain.example.exception.ExampleException;

public class MailingNotFoundException extends MailException {
    public MailingNotFoundException() {
        super(MailErrorCode.MAILING_NOT_FOUND);
    }
}