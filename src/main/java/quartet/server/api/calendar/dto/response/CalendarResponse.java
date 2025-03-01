package quartet.server.api.calendar.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import quartet.server.domain.certification.type.ExamType;
import quartet.server.domain.certification.type.ScheduleType;

import java.time.Instant;
import java.util.List;

public record CalendarResponse(
        long certificationId,
        String certificationName,
        List<CalendarScheduleResponse> schedules
) {
        @QueryProjection
        public CalendarResponse(
                long certificationId,
                String certificationName,
                List<CalendarScheduleResponse> schedules
        ) {
                this.certificationId = certificationId;
                this.certificationName = certificationName;
                this.schedules = schedules;
        }

        public record CalendarScheduleResponse(
                ScheduleType scheduleType,
                ExamType examType,
                Instant date
        ) {
                @QueryProjection
                public CalendarScheduleResponse(
                        ScheduleType scheduleType,
                        ExamType examType,
                        Instant date
                ) {
                        this.scheduleType = scheduleType;
                        this.examType = examType;
                        this.date = date;
                }
        }
}