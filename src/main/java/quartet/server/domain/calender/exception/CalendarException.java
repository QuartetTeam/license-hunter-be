package quartet.server.domain.calender.exception;

import quartet.server.core.code.ResponseCode;
import quartet.server.core.exception.BaseException;

public class CalendarException extends BaseException {
    public CalendarException(final ResponseCode errorCode) {
        super(errorCode);
    }
}