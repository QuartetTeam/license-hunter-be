package quartet.server.domain.mail.exception;

import quartet.server.core.code.ExampleErrorCode;
import quartet.server.core.code.MailErrorCode;
import quartet.server.domain.example.exception.ExampleException;

public class MailAlarmNotFoundException extends ExampleException {
    public MailAlarmNotFoundException() {
        super(MailErrorCode.MAIL_ALARM_NOT_FOUND);
    }
}