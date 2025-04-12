package quartet.server.api.calendar.dto.response;

import java.time.Instant;
import java.util.List;

public record CalendarResponse(
        long certificationId,
        long calendarId,
        String certificationName,
        List<CalendarScheduleResponse> schedules
) {
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
        public CalendarScheduleResponse(
                String scheduleType,
                String examType,
                String examRound,
                List<Instant> date
        ) {
            this.scheduleType = scheduleType;
            this.examType = examType;
            this.examRound = examRound;
            this.date = date;
        }

        public static CalendarScheduleResponse of(
                String scheduleType,
                String examType,
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