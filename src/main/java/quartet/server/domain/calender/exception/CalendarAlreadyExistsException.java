package quartet.server.domain.calender.exception;

import quartet.server.core.code.CalendarErrorCode;

public class CalendarAlreadyExistsException extends CalendarException {
    public CalendarAlreadyExistsException() {
        super(CalendarErrorCode.CALENDAR_ALREADY_EXISTS);
    }
}