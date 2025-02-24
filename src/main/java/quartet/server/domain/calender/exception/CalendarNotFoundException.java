package quartet.server.domain.calender.exception;

import quartet.server.core.code.CalendarErrorCode;
import quartet.server.core.code.ExampleErrorCode;
import quartet.server.domain.example.exception.ExampleException;

public class CalendarNotFoundException extends ExampleException {
    public CalendarNotFoundException() {
        super(CalendarErrorCode.CALENDAR_NOT_FOUND);
    }
}