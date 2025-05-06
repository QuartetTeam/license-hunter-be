package quartet.server.api.calendar.fixture;

import quartet.server.api.calendar.dto.response.CalendarResponse;
import quartet.server.api.calendar.dto.response.ScheduleKey;
import quartet.server.domain.certification.type.ExamType;
import quartet.server.domain.certification.type.ScheduleGroup;
import quartet.server.domain.certification.type.ScheduleType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class CalendarFixture {

    public static List<CalendarResponse> mockCalendarResponses() {
        return List.of(
                CalendarResponse.of(
                        1L,
                        101L,
                        "정보처리기사",
                        List.of()
                )
        );
    }

    public static Map<Long, Map<ScheduleKey, List<LocalDateTime>>> mockSchedulesByDateRange() {
        ScheduleKey applicationKey = new ScheduleKey(ScheduleGroup.APPLICATION.getValue(), ExamType.WRITTEN, "1회");
        ScheduleKey examKey = new ScheduleKey(ScheduleGroup.EXAM.getValue(), ExamType.WRITTEN, "1회");
        ScheduleKey passKey = new ScheduleKey(ScheduleGroup.PASS.getValue(), ExamType.WRITTEN, "1회");

        Map<ScheduleKey, List<LocalDateTime>> scheduleMap = Map.of(
                applicationKey, List.of(
                        LocalDateTime.parse("2024-01-15T00:00:00"), // 범위 밖 날짜
                        LocalDateTime.parse("2024-02-01T00:00:00"),
                        LocalDateTime.parse("2024-02-07T00:00:00")
                ),
                examKey, List.of(
                        LocalDateTime.parse("2024-03-01T00:00:00"),
                        LocalDateTime.parse("2024-03-06T00:00:00"),
                        LocalDateTime.parse("2024-05-10T00:00:00")  // 범위 밖 날짜
                ),
                passKey, List.of(
                        LocalDateTime.parse("2024-04-09T00:00:00"),
                        LocalDateTime.parse("2024-06-01T00:00:00")  // 범위 밖 날짜

                )
        );

        return Map.of(1L, scheduleMap);
    }

    public static List<CalendarResponse> calendarResponses() {
        return List.of(
                CalendarResponse.of(
                        1L,
                        101L,
                        "정보처리기사",
                        List.of(
                                CalendarResponse.CalendarScheduleResponse.of(
                                        ScheduleGroup.APPLICATION.getValue(),
                                        ExamType.WRITTEN,
                                        "1회",
                                        List.of(
                                                LocalDateTime.parse("2024-01-15T00:00:00"),
                                                LocalDateTime.parse("2024-02-01T00:00:00"),
                                                LocalDateTime.parse("2024-02-07T00:00:00")
                                        )
                                ),
                                CalendarResponse.CalendarScheduleResponse.of(
                                        ScheduleGroup.EXAM.getValue(),
                                        ExamType.WRITTEN,
                                        "1회",
                                        List.of(
                                                LocalDateTime.parse("2024-03-01T00:00:00"),
                                                LocalDateTime.parse("2024-03-06T00:00:00"),
                                                LocalDateTime.parse("2024-05-10T00:00:00")
                                        )
                                ),
                                CalendarResponse.CalendarScheduleResponse.of(
                                        ScheduleGroup.PASS.getValue(),
                                        ExamType.WRITTEN,
                                        "1회",
                                        List.of(
                                                LocalDateTime.parse("2024-04-09T00:00:00"),
                                                LocalDateTime.parse("2024-06-01T00:00:00")
                                        )
                                )
                        )
                )
        );
    }
}