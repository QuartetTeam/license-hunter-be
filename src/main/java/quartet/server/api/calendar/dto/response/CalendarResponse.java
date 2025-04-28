package quartet.server.api.calendar.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import quartet.server.domain.certification.type.ExamType;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public record CalendarResponse(
        long certificationId,
        long calendarId,
        String name,
        List<CalendarScheduleResponse> schedules
) {
    @QueryProjection
    public CalendarResponse(
        long certificationId,
        long calendarId,
        String name
    ) {
        this(
                certificationId,
                calendarId,
                name,
                Collections.emptyList()
        );
    }

    public static CalendarResponse of(
            long certificationId,
            long calendarId,
            String certificationName,
            List<CalendarScheduleResponse> schedules
    ) {
        return new CalendarResponse(
                certificationId,
                calendarId,
                certificationName,
                schedules
        );
    }

    public record CalendarScheduleResponse(
            String scheduleType,
            String examType,
            String examRound,
            List<Instant> date
    ) {
        @QueryProjection
        public CalendarScheduleResponse(
                String scheduleType,
                ExamType examType,
                String examRound,
                List<Instant> date
        ) {
            this(
                    scheduleType,
                    examType != null ? examType.getValue() : "",
                    examRound,
                    date
            );
        }

        public static CalendarScheduleResponse of(
                String scheduleType,
                ExamType examType,
                String examRound,
                List<Instant> date
        ) {
            return new CalendarScheduleResponse(
                    scheduleType,
                    examType,
                    examRound,
                    date
            );
        }
    }
}