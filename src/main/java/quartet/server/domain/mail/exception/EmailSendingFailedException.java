package quartet.server.domain.mail.exception;

import quartet.server.core.code.MailErrorCode;

public class EmailSendingFailedException extends MailException {
    public EmailSendingFailedException(Throwable cause) {
        super(MailErrorCode.EMAIL_SENDING_FAILED, cause);
    }
}