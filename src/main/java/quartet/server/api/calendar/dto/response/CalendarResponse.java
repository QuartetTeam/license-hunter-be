package quartet.server.api.calendar.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import quartet.server.domain.certification.type.ExamType;
import quartet.server.domain.certification.type.ScheduleType;

import java.time.Instant;
import java.util.List;

public record CalendarResponse(
        long certificationId,
        long calendarId,
        String certificationName,
        List<CalendarScheduleResponse> schedules
) {
        @QueryProjection
        public CalendarResponse(
                long certificationId,
                long calendarId,
                String certificationName,
                List<CalendarScheduleResponse> schedules
        ) {
                this.certificationId = certificationId;
                this.calendarId = calendarId;
                this.certificationName = certificationName;
                this.schedules = schedules;
        }

        public record CalendarScheduleResponse(
                String scheduleType,
                String examType,
                Instant date,
                String examRound
        ) {
                @QueryProjection
                public CalendarScheduleResponse(
                        ScheduleType scheduleType,
                        ExamType examType,
                        Instant date,
                        String examRound
                ) {
                        this(
                                scheduleType.getValue(),
                                examType.getValue(),
                                date,
                                examRound
                        );
                }
        }
}