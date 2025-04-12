package quartet.server.api.calendar.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import quartet.server.domain.certification.type.ExamType;
import quartet.server.domain.certification.type.ScheduleType;

import java.time.Instant;

public record CalendarProjection(
        long certificationId,
        long calendarId,
        String certificationName,
        ScheduleType scheduleType,
        ExamType examType,
        String examRound,
        Instant date
) {
        @QueryProjection
        public CalendarProjection {
        }
}