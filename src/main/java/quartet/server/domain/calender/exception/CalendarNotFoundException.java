package quartet.server.domain.calender.exception;

import quartet.server.core.code.CalendarErrorCode;

public class CalendarNotFoundException extends CalendarException {
    public CalendarNotFoundException() {
        super(CalendarErrorCode.CALENDAR_NOT_FOUND);
    }
}