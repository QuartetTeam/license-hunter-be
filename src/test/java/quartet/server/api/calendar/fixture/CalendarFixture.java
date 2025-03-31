package quartet.server.api.calendar.fixture;

import quartet.server.api.calendar.dto.response.CalendarResponse;
import quartet.server.domain.certification.type.ExamType;
import quartet.server.domain.certification.type.ScheduleType;

import java.time.Instant;
import java.util.List;

public class CalendarFixture {

    public static List<CalendarResponse.CalendarScheduleResponse> calendarScheduleResponses() {
        return List.of(
                new CalendarResponse.CalendarScheduleResponse(
                        ScheduleType.APPLICATION_START, ExamType.WRITTEN, Instant.parse("2025-01-01T00:00:00Z"), "1회차"
                ),
                new CalendarResponse.CalendarScheduleResponse(
                        ScheduleType.APPLICATION_END, ExamType.WRITTEN, Instant.parse("2025-01-15T00:00:00Z"), "1회차"
                ),
                new CalendarResponse.CalendarScheduleResponse(
                        ScheduleType.EXAM_START, ExamType.WRITTEN, Instant.parse("2025-01-15T00:00:00Z"), "1회차"
                ),
                new CalendarResponse.CalendarScheduleResponse(
                        ScheduleType.EXAM_END, ExamType.WRITTEN, Instant.parse("2025-01-15T00:00:00Z"), "1회차"
                ),
                new CalendarResponse.CalendarScheduleResponse(
                        ScheduleType.PASS_ANNOUNCEMENT, ExamType.WRITTEN, Instant.parse("2025-02-01T00:00:00Z"), "1회차"
                )
        );
    }

    public static List<CalendarResponse.CalendarScheduleResponse> practicalCalendarScheduleResponses() {
        return List.of(
                new CalendarResponse.CalendarScheduleResponse(
                        ScheduleType.APPLICATION_START, ExamType.PRACTICAL, Instant.parse("2025-03-01T00:00:00Z"), "1회차"
                ),
                new CalendarResponse.CalendarScheduleResponse(
                        ScheduleType.APPLICATION_END, ExamType.PRACTICAL, Instant.parse("2025-03-15T00:00:00Z"), "1회차"
                )
        );
    }

    public static List<CalendarResponse> calendarResponses() {
        return List.of(
                new CalendarResponse(1L, 1L,"정보처리기사", calendarScheduleResponses()),
                new CalendarResponse(2L, 2L,"SQL 개발자", practicalCalendarScheduleResponses()),
                new CalendarResponse(3L, 3L,"리눅스마스터", List.of(
                        new CalendarResponse.CalendarScheduleResponse(
                                ScheduleType.APPLICATION_END, ExamType.WRITTEN, Instant.parse("2025-05-01T00:00:00Z"),"1회차"
                        )
                ))
        );
    }
}