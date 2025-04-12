package quartet.server.api.calendar.fixture;

import quartet.server.api.calendar.dto.response.CalendarProjection;
import quartet.server.api.calendar.dto.response.CalendarResponse;
import quartet.server.domain.certification.type.ExamType;
import quartet.server.domain.certification.type.ScheduleGroup;
import quartet.server.domain.certification.type.ScheduleType;

import java.time.Instant;
import java.util.List;

public class CalendarFixture {

    public static List<CalendarProjection> calendarProjections() {
        return List.of(
                new CalendarProjection(1L, 101L, "정보처리기사", ScheduleType.APPLICATION_START, ExamType.WRITTEN, "1회", Instant.parse("2024-02-01T00:00:00Z")),
                new CalendarProjection(1L, 101L, "정보처리기사", ScheduleType.APPLICATION_END, ExamType.WRITTEN, "1회", Instant.parse("2024-02-07T00:00:00Z")),
                new CalendarProjection(1L, 101L, "정보처리기사", ScheduleType.EXAM_START, ExamType.WRITTEN, "1회", Instant.parse("2024-03-01T00:00:00Z")),
                new CalendarProjection(1L, 101L, "정보처리기사", ScheduleType.EXAM_END, ExamType.WRITTEN, "1회", Instant.parse("2024-03-06T00:00:00Z")),
                new CalendarProjection(1L, 101L, "정보처리기사", ScheduleType.PASS_ANNOUNCEMENT, ExamType.WRITTEN, "1회", Instant.parse("2024-04-09T00:00:00Z")));
    }

    public static List<CalendarResponse> calendarResponses() {
        return List.of(
                CalendarResponse.of(
                        1L,
                        101L,
                        "정보처리기사",
                        List.of(
                                CalendarResponse.CalendarScheduleResponse.of(ScheduleGroup.APPLICATION.getValue(), ExamType.WRITTEN.getValue(), "1회", List.of(
                                        Instant.parse("2024-02-01T00:00:00Z"),
                                        Instant.parse("2024-02-07T00:00:00Z")
                                )),
                                CalendarResponse.CalendarScheduleResponse.of(ScheduleGroup.EXAM.getValue(), ExamType.WRITTEN.getValue(), "1회", List.of(
                                        Instant.parse("2024-03-01T00:00:00Z"),
                                        Instant.parse("2024-03-06T00:00:00Z")
                                )),
                                CalendarResponse.CalendarScheduleResponse.of(ScheduleGroup.PASS.getValue(), ExamType.WRITTEN.getValue(), "1회", List.of(
                                        Instant.parse("2024-04-09T00:00:00Z")
                                ))
                        )
                )
        );
    }
}